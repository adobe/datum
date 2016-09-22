/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated.
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.adobe.datum.client;

import com.adobe.datum.client.handler.StreamStartHandler;
import com.adobe.datum.common.channel.ChannelUtil;
import com.adobe.datum.common.channel.ConnectionSettings;
import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.common.function.DatumConsumer;
import com.adobe.datum.common.function.DatumSupplier;
import com.adobe.datum.common.function.UncaughtErrorReporterCallback;
import com.adobe.datum.common.handler.ChannelHandlers;
import com.adobe.datum.common.handler.DatumChannelDownloadHandler;
import com.adobe.datum.common.handler.DatumChannelUploadHandler;
import com.adobe.datum.common.handler.DatumMessageEncoder;
import com.adobe.datum.common.serialize.DatumDeserializer;
import com.adobe.datum.common.serialize.DatumSerializer;
import com.adobe.datum.common.serialize.SerializationContext;
import com.adobe.datum.common.serialize.SerializerProvider;
import com.adobe.datum.common.util.Nullables;
import com.adobe.datum.common.util.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import static com.adobe.datum.common.api.DatumProto.DatumRequestProto.RequestType;

/**
 * @author Adobe Systems Inc.
 */
public final class DatumClient {

  private static final Logger LOG    = LoggerFactory.getLogger(DatumClient.class);
  private static final Random RANDOM = new Random();

  private final ConnectionSettings connectionSettings;
  private final SerializerProvider serializerProvider;

  private final EventLoopGroup workerGroup = ChannelUtil.newEventLoopGroup();
  private final String         clientId    = String.format("%08x", RANDOM.nextInt());

  public DatumClient() {
    this(new SerializationContext());
  }

  public DatumClient(SerializationContext serializationContext) {
    this(ConnectionSettings.getDefaultSettings(), serializationContext);
  }

  public DatumClient(ConnectionSettings connectionSettings) {
    this(connectionSettings, new SerializationContext());
  }

  public DatumClient(ConnectionSettings connectionSettings, SerializationContext serializationContext) {
    this.connectionSettings = connectionSettings;
    serializerProvider = new SerializerProvider(serializationContext);
    LOG.info("datum client started [id: 0x{}]", clientId);
  }

  public <P> void sendDownloadRequest(Object request, Class<P> payloadType, DatumConsumer<P> consumer)
      throws DatumClientException {
    sendDownloadRequest(request, payloadType, consumer, new UncaughtErrorReporterCallback(getClass()));
  }

  @SuppressWarnings("unchecked")
  public <R, P> void sendDownloadRequest(R request,
                                         Class<P> payloadClass,
                                         DatumConsumer<P> consumer,
                                         DatumCallback callback)
      throws DatumClientException {
    DatumSerializer<R> serializer = serializerProvider.getSerializer((Class<R>) request.getClass());
    Channel channel = newChannel();
    sendRequest(channel, request, serializer, payloadClass, RequestType.DOWNLOAD, new DatumCallback() {
      @Override
      public void onComplete() {
        DatumChannelDownloadHandler<P> handler = new DatumChannelDownloadHandler<>(payloadClass, consumer, callback);
        DatumDeserializer<P> payloadDeserializer = serializerProvider.getDeserializer(payloadClass);
        Arrays.asList(ChannelHandlers.inboundHandlers(payloadClass, payloadDeserializer, handler))
              .forEach(channel.pipeline()::addLast);
      }

      @Override
      public void onError(Throwable cause) {
        callback.onError(cause);
      }
    });
  }

  public <P> void sendUploadRequest(Object request, Class<P> payloadType, DatumSupplier<Optional<P>> supplier)
      throws DatumClientException {
    sendUploadRequest(request, payloadType, supplier, new UncaughtErrorReporterCallback(getClass()));
  }

  @SuppressWarnings("unchecked")
  public <R, P> void sendUploadRequest(R request,
                                       Class<P> payloadClass,
                                       DatumSupplier<Optional<P>> supplier,
                                       DatumCallback callback)
      throws DatumClientException {
    DatumSerializer<R> serializer = serializerProvider.getSerializer((Class<R>) request.getClass());
    Channel channel = newChannel();
    sendRequest(channel, request, serializer, payloadClass, RequestType.UPLOAD, new DatumCallback() {
      @Override
      public void onComplete() {
        DatumChannelUploadHandler messageHandler = new DatumChannelUploadHandler<>(supplier, callback);
        DatumSerializer<P> payloadSerializer = serializerProvider.getSerializer(payloadClass);
        channel.pipeline()
               .addLast(DatumMessageEncoder.of(payloadClass, payloadSerializer))
               .addLast(messageHandler);
      }

      @Override
      public void onError(Throwable cause) {
        callback.onError(cause);
      }
    });
  }

  private <R, P> void sendRequest(Channel channel,
                                  R request,
                                  DatumSerializer<R> requestSerializer,
                                  Class<P> payloadType,
                                  RequestType type,
                                  DatumCallback callback)
      throws DatumClientException {
    try {
      ChannelPromise promise = channel.newPromise();
      channel.writeAndFlush(DatumClientUtil.buildDatumRequest(request, requestSerializer, payloadType, type), promise);
      promise.addListener(future -> {
        if (future.isSuccess()) {
          LOG.info("{} request for {} sent successfully", type.name().toLowerCase(), payloadType);
          channel.pipeline().remove(ChannelHandlers.requestEncoder());
          waitForStreamStart(channel, callback);
        } else {
          LOG.info(type.name().toLowerCase() + " request for " + payloadType + " did not complete", future.cause());
          callback.onError(future.cause());
        }
      });
    } catch (Throwable e) {
      channel.close();
      throw new DatumClientException("unable to send datum request", e);
    }
  }

  private void waitForStreamStart(final Channel channel, final DatumCallback callback) {
    channel.pipeline().addLast(new StreamStartHandler(callback));
  }

  private Channel newChannel() throws DatumClientException {
    try {
      return DatumClientBootstrap.getInstance().bootstrap(workerGroup, connectionSettings);
    } catch (InterruptedException e) {
      throw new DatumClientException("unable to get communication channel", e);
    }
  }

  public <T> DatumClient addSerializer(DatumSerializer<T> serializer) {
    addSerializer(serializer, ReflectionUtil.getFirstTypeParameter(serializer.getClass()));
    return this;
  }

  public <T> DatumClient addSerializer(DatumSerializer<T> serializer, List<Class<? extends T>> classes) {
    Nullables.getOrDefault(classes, Collections.<Class<? extends T>>emptyList())
             .forEach(clazz -> addSerializer(serializer, clazz));
    return this;
  }

  public <T> DatumClient addSerializer(DatumSerializer<T> serializer, Class<? extends T> clazz) {
    serializerProvider.getSerializationContext().register(clazz, serializer);
    return this;
  }

  public <T> DatumClient addDeserializer(DatumDeserializer<T> deserializer) {
    addDeserializer(deserializer, ReflectionUtil.getFirstTypeParameter(deserializer.getClass()));
    return this;
  }

  public <T> DatumClient addDeserializer(DatumDeserializer<T> deserializer, List<Class<? extends T>> classes) {
    Nullables.getOrDefault(classes, Collections.<Class<? extends T>>emptyList())
             .forEach(clazz -> addDeserializer(deserializer, clazz));
    return this;
  }

  public <T> DatumClient addDeserializer(DatumDeserializer<T> deserializer, Class<? extends T> clazz) {
    serializerProvider.getSerializationContext().register(clazz, deserializer);
    return this;
  }

  public void shutdown() {
    shutdown(new UncaughtErrorReporterCallback(getClass()));
  }

  public void shutdown(DatumCallback callback) {
    LOG.info("shutting down datum client [id: 0x{}]", clientId);
    workerGroup.shutdownGracefully().addListener(future -> {
      if (future.isSuccess()) {
        LOG.info("datum client shut down successfully [id: 0x{}]", clientId);
        callback.onComplete();
      } else {
        LOG.info("error while shutting down datum client [id: 0x" + clientId + "]", future.cause());
        callback.onError(future.cause());
      }
    });
  }
}
