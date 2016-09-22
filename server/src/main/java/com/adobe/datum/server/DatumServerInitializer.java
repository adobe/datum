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

import com.adobe.datum.common.channel.ConnectionSettings;
import com.adobe.datum.common.handler.ChannelHandlers;
import com.adobe.datum.server.handler.DatumServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Adobe Systems Inc.
 */
public class DatumServerInitializer extends ChannelInitializer<SocketChannel> {

  private final DatumServerHandler serverHandler;
  private final ConnectionSettings settings;

  public DatumServerInitializer(DatumServerHandler serverHandler, ConnectionSettings settings) {
    this.serverHandler = serverHandler;
    this.settings = settings;
  }

  @Override
  public void initChannel(SocketChannel channel) {
    channel.pipeline()
           .addLast(ChannelHandlers.loggingHandler())
           .addLast(ChannelHandlers.trafficShapingHandlers(settings))
           .addLast(ChannelHandlers.metadataEncoder())
           .addLast(ChannelHandlers.frameDecoder())
           .addLast(ChannelHandlers.frameEncoder())
           .addLast(ChannelHandlers.requestDecoder())
           .addLast(serverHandler);
  }
}
