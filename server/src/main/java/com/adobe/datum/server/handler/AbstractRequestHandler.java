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

import com.adobe.datum.common.channel.metadata.ChannelMetadata;
import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.common.handler.ChannelHandlers;
import com.adobe.datum.common.serialize.DatumDeserializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.FutureListener;
import static com.adobe.datum.common.api.DatumProto.DatumRequestProto;

/**
 * @author Adobe Systems Inc.
 * @param <R> Type of request that can be handled by this request handler.
 * @param <P> Payload type for the objects in stream.
 */
public abstract class AbstractRequestHandler<R, P> implements RequestHandler {

  private final Class<P>             payloadClass;
  private final DatumDeserializer<R> requestDeserializer;
  private final DatumCallback        callback;

  protected AbstractRequestHandler(Class<P> payloadClass,
                                   DatumDeserializer<R> requestDeserializer,
                                   DatumCallback callback) {
    this.payloadClass = payloadClass;
    this.requestDeserializer = requestDeserializer;
    this.callback = callback;
  }

  @Override
  public final void handleRequest(ChannelHandlerContext context, DatumRequestProto datumRequest) {
    try {
      R request = requestDeserializer.deserialize(datumRequest.getParameterBody());
      doRequest(context, request);
    } catch (Throwable t) {
      handleError(new RequestHandlerException("unable to deserialize request: " + datumRequest.getParameterBody()
                                                  + " using " + requestDeserializer, t));
    }
  }

  private void handleError(Throwable cause) {
    callback.onError(cause);
  }

  protected void prepareChannel(Channel channel, FutureListener listener) {
    // cleanup inbound channel handlers
    channel.pipeline()
           .remove(ChannelHandlers.requestDecoder())
           .remove(DatumServerHandler.class);

    // send message to indicate start of streaming
    sendStreamStartMessage(channel, listener);
  }

  @SuppressWarnings("unchecked")
  private void sendStreamStartMessage(Channel channel, FutureListener listener) {
    ChannelPromise promise = channel.newPromise().addListener(listener);
    channel.writeAndFlush(ChannelMetadata.streamStartMessage(), promise);
  }

  public Class<P> getPayloadClass() {
    return payloadClass;
  }

  protected DatumCallback getCallback() {
    return callback;
  }

  protected abstract void doRequest(ChannelHandlerContext context, R request) throws RequestHandlerException;

}
