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

package com.adobe.datum.common.channel;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.SystemUtils;

/**
 * @author Adobe Systems Inc.
 */
public final class ChannelUtil {

  private ChannelUtil() { }

  public static EventLoopGroup newEventLoopGroup() {
    return newEventLoopGroup(0);
  }

  public static EventLoopGroup newEventLoopGroup(int numThreads) {
    return SystemUtils.IS_OS_LINUX ? new EpollEventLoopGroup(numThreads) : new NioEventLoopGroup(numThreads);
  }

  public static Class<? extends ServerChannel> getServerChannel() {
    return SystemUtils.IS_OS_LINUX ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
  }

  public static Class<? extends Channel> getClientChannel() {
    return SystemUtils.IS_OS_LINUX ? EpollSocketChannel.class : NioSocketChannel.class;
  }

  public static ByteBufAllocator getAllocator() {
    return PooledByteBufAllocator.DEFAULT;
  }

  public static void validateSettings(ConnectionSettings settings) {
    Preconditions.checkArgument(settings.getConnectTimeoutMillis() > settings.getBandwidthWaitDelay(),
                                "bandwidthWaitDelay should be less than connection timeout");
  }
}
