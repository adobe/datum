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
import com.adobe.datum.common.function.DatumConsumer;
import com.adobe.datum.common.handler.DatumChannelDownloadHandler;
import com.adobe.datum.common.serialize.DatumDeserializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FutureListener;
import static com.adobe.datum.common.handler.ChannelHandlers.inboundHandlers;

/**
 * @author Adobe Systems Inc.
 */
final class UploadRequestHandler<R, P> extends AbstractRequestHandler<R, P> {

  private final UploadHandler<R, P>  uploadHandler;
  private final DatumDeserializer<P> payloadDeserializer;

  UploadRequestHandler(Class<P> payloadClass,
                       UploadHandler<R, P> uploadHandler,
                       DatumDeserializer<R> requestDeserializer,
                       DatumDeserializer<P> payloadDeserializer,
                       DatumCallback callback) {
    super(payloadClass, requestDeserializer, callback);
    this.uploadHandler = uploadHandler;
    this.payloadDeserializer = payloadDeserializer;
  }

  @Override
  protected void doRequest(ChannelHandlerContext context, R request) throws RequestHandlerException {
    DatumConsumer<P> consumer = uploadHandler.handleRequest(request);
    Class<P> payloadClass = getPayloadClass();
    ChannelHandler channelHandler = DatumChannelDownloadHandler.of(payloadClass, consumer, getCallback());
    FutureListener listener =
        future -> context.pipeline().addLast(inboundHandlers(payloadClass, payloadDeserializer, channelHandler));
    prepareChannel(context.channel(), listener);
  }
}
