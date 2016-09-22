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

package com.adobe.datum.server.handler;

import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.common.serialize.DatumDeserializer;
import com.adobe.datum.common.serialize.DatumSerializer;
import com.adobe.datum.common.serialize.SerializationContext;
import com.adobe.datum.common.serialize.SerializerProvider;
import com.adobe.datum.common.util.ReflectionUtil;
import com.google.common.collect.Lists;
import java.util.List;
import static com.adobe.datum.common.api.DatumProto.DatumRequestProto.RequestType;

/**
 * @author Adobe Systems Inc.
 */
public class DatumServerHandlerBuilder {

  private final List<DownloadRequestHandlerBuilder> downloadRequestHandlerBuilders = Lists.newArrayList();
  private final List<UploadRequestHandlerBuilder>   uploadRequestHandlerBuilders   = Lists.newArrayList();

  private final DatumCallback callback;

  public DatumServerHandlerBuilder(DatumCallback callback) {
    this.callback = callback;
  }

  public <R, P> void addDownloadHandler(DownloadHandler<R, P> downloadHandler, DatumCallback callback) {
    Class<R> requestClass = ReflectionUtil.getFirstTypeParameter(downloadHandler.getClass());
    Class<P> payloadClass = ReflectionUtil.getSecondTypeParameter(downloadHandler.getClass());
    DownloadRequestHandlerBuilder<R, P> builder = new DownloadRequestHandlerBuilder<>(requestClass, payloadClass);
    builder.handler(downloadHandler)
           .callback(callback)
           .requestType(RequestType.DOWNLOAD);
    downloadRequestHandlerBuilders.add(builder);
  }

  public <R, P> void addUploadHandler(UploadHandler<R, P> uploadHandler, DatumCallback callback) {
    Class<R> requestClass = ReflectionUtil.getFirstTypeParameter(uploadHandler.getClass());
    Class<P> payloadClass = ReflectionUtil.getSecondTypeParameter(uploadHandler.getClass());
    UploadRequestHandlerBuilder<R, P> builder = new UploadRequestHandlerBuilder<>(requestClass, payloadClass);
    builder.handler(uploadHandler)
           .callback(callback)
           .requestType(RequestType.UPLOAD);
    uploadRequestHandlerBuilders.add(builder);
  }

  @SuppressWarnings("unchecked")
  public DatumServerHandler build(SerializationContext serializationContext) {
    SerializerProvider serializerProvider = new SerializerProvider(serializationContext);
    DatumServerHandler serverHandler = new DatumServerHandler(callback);
    downloadRequestHandlerBuilders.forEach(builder -> {
      builder.payloadSerializer(serializerProvider.getSerializer(builder.getPayloadClass()))
             .requestDeserializer(serializerProvider.getDeserializer(builder.getRequestClass()));
      serverHandler.addRequestHandler(builder.buildRequestHandlerKey(), builder.buildRequestHandler());
    });
    uploadRequestHandlerBuilders.forEach(builder -> {
      builder.payloadDeserializer(serializerProvider.getDeserializer(builder.getPayloadClass()))
             .requestDeserializer(serializerProvider.getDeserializer(builder.getRequestClass()));
      serverHandler.addRequestHandler(builder.buildRequestHandlerKey(), builder.buildRequestHandler());
    });
    return serverHandler;
  }

  private abstract static class RequestHandlerBuilder<R, P> {

    final Class<R> requestClass;
    final Class<P> payloadClass;

    DatumCallback        callback;
    RequestType          requestType;
    DatumDeserializer<R> requestDeserializer;

    public RequestHandlerBuilder(Class<R> requestClass, Class<P> payloadClass) {
      this.requestClass = requestClass;
      this.payloadClass = payloadClass;
    }

    public RequestHandlerBuilder<R, P> callback(DatumCallback callback) {
      this.callback = callback;
      return this;
    }

    public RequestHandlerBuilder<R, P> requestType(RequestType requestType) {
      this.requestType = requestType;
      return this;
    }

    public RequestHandlerBuilder<R, P> requestDeserializer(DatumDeserializer<R> requestDeserializer) {
      this.requestDeserializer = requestDeserializer;
      return this;
    }

    public Class<R> getRequestClass() {
      return requestClass;
    }

    public Class<P> getPayloadClass() {
      return payloadClass;
    }

    public RequestHandlerKey buildRequestHandlerKey() {
      return new RequestHandlerKey(requestClass.getName(), payloadClass.getName(), requestType);
    }
  }

  private static final class DownloadRequestHandlerBuilder<R, P> extends RequestHandlerBuilder<R, P> {

    private DownloadHandler<R, P> downloadHandler;
    private DatumSerializer<P>    payloadSerializer;

    public DownloadRequestHandlerBuilder(Class<R> requestClass, Class<P> payloadClass) {
      super(requestClass, payloadClass);
    }

    public DownloadRequestHandlerBuilder<R, P> handler(DownloadHandler<R, P> downloadHandler) {
      this.downloadHandler = downloadHandler;
      return this;
    }

    public DownloadRequestHandlerBuilder<R, P> payloadSerializer(DatumSerializer<P> payloadSerializer) {
      this.payloadSerializer = payloadSerializer;
      return this;
    }

    public DownloadRequestHandler<R, P> buildRequestHandler() {
      return new DownloadRequestHandler<>(payloadClass,
                                          downloadHandler,
                                          requestDeserializer,
                                          payloadSerializer,
                                          callback);
    }
  }

  private static final class UploadRequestHandlerBuilder<R, P> extends RequestHandlerBuilder<R, P> {

    private UploadHandler<R, P>  uploadHandler;
    private DatumDeserializer<P> payloadDeserializer;

    public UploadRequestHandlerBuilder(Class<R> requestClass, Class<P> payloadClass) {
      super(requestClass, payloadClass);
    }

    public UploadRequestHandlerBuilder handler(UploadHandler<R, P> uploadHandler) {
      this.uploadHandler = uploadHandler;
      return this;
    }

    public UploadRequestHandlerBuilder<R, P> payloadDeserializer(DatumDeserializer<P> payloadDeserializer) {
      this.payloadDeserializer = payloadDeserializer;
      return this;
    }

    public RequestHandler buildRequestHandler() {
      return new UploadRequestHandler<>(payloadClass,
                                        uploadHandler,
                                        requestDeserializer,
                                        payloadDeserializer,
                                        callback);
    }
  }
}
