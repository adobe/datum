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
import com.adobe.datum.common.function.DatumSupplier;
import com.adobe.datum.common.handler.DatumChannelUploadHandler;
import com.adobe.datum.common.handler.DatumMessageEncoder;
import com.adobe.datum.common.serialize.DatumDeserializer;
import com.adobe.datum.common.serialize.DatumSerializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FutureListener;
import java.util.Optional;

/**
 * @author Adobe Systems Inc.
 */
final class DownloadRequestHandler<R, P> extends AbstractRequestHandler<R, P> {

  private final DownloadHandler<R, P> downloadHandler;
  private final DatumSerializer<P>    payloadSerializer;

  DownloadRequestHandler(Class<P> payloadClass,
                         DownloadHandler<R, P> downloadHandler,
                         DatumDeserializer<R> requestDeserializer,
                         DatumSerializer<P> payloadSerializer,
                         DatumCallback callback) {
    super(payloadClass, requestDeserializer, callback);
    this.downloadHandler = downloadHandler;
    this.payloadSerializer = payloadSerializer;
  }

  @Override
  protected void doRequest(ChannelHandlerContext context, R request) throws RequestHandlerException {
    DatumSupplier<Optional<P>> supplier = downloadHandler.handleRequest(request);
    ChannelHandler channelHandler = DatumChannelUploadHandler.of(supplier, getCallback());
    FutureListener listener =
        future -> context.pipeline()
                         .addLast(DatumMessageEncoder.of(getPayloadClass(), payloadSerializer))
                         .addLast(channelHandler);
    prepareChannel(context.channel(), listener);
  }
}
