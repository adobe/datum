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

import com.adobe.datum.common.channel.ChannelUtil;
import com.adobe.datum.common.channel.ConnectionSettings;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

/**
 * @author Adobe Systems Inc.
 */
final class DatumClientBootstrap {

  private static final DatumClientBootstrap INSTANCE = new DatumClientBootstrap();

  private DatumClientBootstrap() { }

  public static DatumClientBootstrap getInstance() {
    return INSTANCE;
  }

  public Channel bootstrap(EventLoopGroup workerGroup, ConnectionSettings settings) throws InterruptedException {
    ChannelUtil.validateSettings(settings);
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(workerGroup)
             .channel(ChannelUtil.getClientChannel())
             .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, settings.getConnectTimeoutMillis())
             .option(ChannelOption.ALLOCATOR, ChannelUtil.getAllocator())
             .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, settings.getWriteBufferLowWaterMark())
             .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, settings.getWriteBufferHighWaterMark())
             .handler(new DatumClientInitializer(settings));

    return bootstrap.connect(settings.getHost(), settings.getPort()).sync().channel();
  }
}
