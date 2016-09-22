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

package com.adobe.datum.server;

import com.adobe.datum.common.channel.ChannelUtil;
import com.adobe.datum.common.channel.ConnectionSettings;
import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.common.function.UncaughtErrorReporterCallback;
import com.adobe.datum.common.serialize.DatumDeserializer;
import com.adobe.datum.common.serialize.DatumSerializer;
import com.adobe.datum.common.serialize.SerializationContext;
import com.adobe.datum.common.util.Nullables;
import com.adobe.datum.common.util.ReflectionUtil;
import com.adobe.datum.server.handler.DatumServerHandler;
import com.adobe.datum.server.handler.DatumServerHandlerBuilder;
import com.adobe.datum.server.handler.DownloadHandler;
import com.adobe.datum.server.handler.UploadHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;

/**
 * @author Adobe Systems Inc.
 */
public final class DatumServer {

  private static final Logger LOG = LoggerFactory.getLogger(DatumServer.class);

  private final DatumServerHandlerBuilder builder;
  private final SerializationContext serializationContext = new SerializationContext();

  public DatumServer(DatumCallback callback) {
    builder = new DatumServerHandlerBuilder(callback);
  }

  public DatumServer() {
    this(new UncaughtErrorReporterCallback(DatumServer.class));
  }

  public DatumServer start(ConnectionSettings settings) throws InterruptedException {
    return start(settings, false);
  }

  public DatumServer start(ConnectionSettings settings, boolean synchronous) throws InterruptedException {
    DatumServerHandler serverHandler = builder.build(serializationContext);
    EventLoopGroup eventLoopGroup = ChannelUtil.newEventLoopGroup();
    ChannelFuture channelFuture = DatumServerBootstrap.getInstance()
                                                      .bootstrap(eventLoopGroup, settings, serverHandler)
                                                      .bind(settings.getPort());
    channelFuture.addListener(future -> LOG.info("datum server started on port {}", settings.getPort()));
    ChannelFuture closeFuture = channelFuture.channel().closeFuture();
    closeFuture.addListener(future -> LOG.info("datum server shutting down"))
               .addListener(future -> eventLoopGroup.shutdownGracefully());
    if (synchronous) {
      closeFuture.sync();
    }
    return this;
  }

  public <R, P> DatumServer addDownloadHandler(DownloadHandler<R, P> downloadHandler) {
    return addDownloadHandler(downloadHandler, new UncaughtErrorReporterCallback(downloadHandler.getClass()));
  }

  public <R, P> DatumServer addDownloadHandler(DownloadHandler<R, P> downloadHandler, DatumCallback callback) {
    builder.addDownloadHandler(downloadHandler, callback);
    return this;
  }

  public <R, P> DatumServer addUploadHandler(UploadHandler<R, P> uploadHandler) {
    return addUploadHandler(uploadHandler, new UncaughtErrorReporterCallback(uploadHandler.getClass()));
  }

  public <R, P> DatumServer addUploadHandler(UploadHandler<R, P> uploadHandler, DatumCallback callback) {
    builder.addUploadHandler(uploadHandler, callback);
    return this;
  }

  public <T> DatumServer addSerializer(DatumSerializer<T> serializer) {
    serializationContext.register(ReflectionUtil.getFirstTypeParameter(serializer.getClass()), serializer);
    return this;
  }

  public <T> DatumServer addSerializer(DatumSerializer<T> serializer, Class<? extends T> clazz) {
    serializationContext.register(clazz, serializer);
    return this;
  }

  public <T> DatumServer addSerializer(DatumSerializer<T> serializer, List<Class<? extends T>> classes) {
    Nullables.getOrDefault(classes, Collections.<Class<T>>emptyList())
             .forEach(serializableClass -> serializationContext.register(serializableClass, serializer));
    return this;
  }

  public <T> DatumServer addDeserializer(DatumDeserializer<T> deserializer) {
    serializationContext.register(ReflectionUtil.getFirstTypeParameter(deserializer.getClass()), deserializer);
    return this;
  }

  public <T> DatumServer addDeserializer(DatumDeserializer<T> deserializer, Class<? extends T> clazz) {
    serializationContext.register(clazz, deserializer);
    return this;
  }

  public <T> DatumServer addDeserializer(DatumDeserializer<T> deserializer, List<Class<? extends T>> classes) {
    Nullables.getOrDefault(classes, Collections.<Class<T>>emptyList())
             .forEach(deserializableClass -> serializationContext.register(deserializableClass, deserializer));
    return this;
  }
}
