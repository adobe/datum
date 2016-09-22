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
import com.adobe.datum.server.handler.DatumServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import static io.netty.channel.ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK;
import static io.netty.channel.ChannelOption.WRITE_BUFFER_LOW_WATER_MARK;

/**
 * @author Adobe Systems Inc.
 */
public final class DatumServerBootstrap {

  private static final DatumServerBootstrap INSTANCE = new DatumServerBootstrap();

  private DatumServerBootstrap() { }

  public static DatumServerBootstrap getInstance() {
    return INSTANCE;
  }

  public ServerBootstrap bootstrap(EventLoopGroup group, ConnectionSettings settings, DatumServerHandler serverHandler)
      throws InterruptedException {
    ChannelUtil.validateSettings(settings);
    return new ServerBootstrap().group(group, group)
                                .channel(ChannelUtil.getServerChannel())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, settings.getConnectTimeoutMillis())
                                .handler(new LoggingHandler(LogLevel.INFO))
                                .childOption(ChannelOption.ALLOCATOR, ChannelUtil.getAllocator())
                                .childOption(WRITE_BUFFER_LOW_WATER_MARK, settings.getWriteBufferLowWaterMark())
                                .childOption(WRITE_BUFFER_HIGH_WATER_MARK, settings.getWriteBufferHighWaterMark())
                                .childHandler(new DatumServerInitializer(serverHandler, settings));
  }
}
