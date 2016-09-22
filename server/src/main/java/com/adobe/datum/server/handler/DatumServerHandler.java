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
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import static com.adobe.datum.common.api.DatumProto.DatumRequestProto;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Adobe Systems Inc.
 */
@ChannelHandler.Sharable
public final class DatumServerHandler extends SimpleChannelInboundHandler<DatumRequestProto> {

  private static final Logger LOG = LoggerFactory.getLogger(DatumServerHandler.class);

  private final Map<RequestHandlerKey, RequestHandler> requestHandlers = Maps.newHashMap();

  private final DatumCallback callback;

  public DatumServerHandler(DatumCallback callback) {
    this.callback = callback;
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext context) {
    context.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
    LOG.error("server caught exception", cause);
    context.close();
    context.executor().submit(() -> callback.onError(cause));
  }

  @Override
  protected void channelRead0(ChannelHandlerContext context, DatumRequestProto datumRequest) {
    handleRequest(context, datumRequest);
    context.executor().submit(callback::onComplete);
  }

  public void addRequestHandler(RequestHandlerKey key, RequestHandler requestHandler) {
    requestHandlers.put(key, requestHandler);
  }

  private void handleRequest(ChannelHandlerContext context, DatumRequestProto request) {
    RequestHandlerKey handlerKey =
        new RequestHandlerKey(request.getParameterType(), request.getPrototypeName(), request.getType());
    checkArgument(requestHandlers.containsKey(handlerKey), "no request handler exists for {}", handlerKey);
    RequestHandler requestHandler = requestHandlers.get(handlerKey);
    requestHandler.handleRequest(context, request);
  }
}
