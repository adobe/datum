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

package com.adobe.datum.common.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Adobe Systems Inc.
 */
public class TrafficLoggingHandler extends ChannelDuplexHandler {

  private static final Logger LOG = LoggerFactory.getLogger(TrafficLoggingHandler.class);

  private static final int MIN_IO_FOR_LOG = 10000;

  private TrafficCounter trafficCounter;

  private final AtomicInteger ioCount = new AtomicInteger();

  @Override
  public void handlerAdded(ChannelHandlerContext context) {
    ChannelTrafficShapingHandler trafficShapingHandler = context.channel()
                                                                .pipeline()
                                                                .get(ChannelTrafficShapingHandler.class);
    trafficCounter = trafficShapingHandler.trafficCounter();
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext context) throws Exception {
    logTraffic();
    super.channelUnregistered(context);
  }

  @Override
  public void channelWritabilityChanged(ChannelHandlerContext context) throws Exception {
    logTraffic();
    super.channelWritabilityChanged(context);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext context) throws Exception {
    if (ioCount.incrementAndGet() % MIN_IO_FOR_LOG == 0) {
      logTraffic();
    }
    super.channelReadComplete(context);
  }

  @Override
  public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
    if (ioCount.incrementAndGet() % MIN_IO_FOR_LOG == 0) {
      logTraffic();
    }
    super.write(context, message, promise);
  }

  private void logTraffic() {
    if (trafficCounter.cumulativeWrittenBytes() > 0) {
      log("Wrote {} bytes @ {} bytes/s", trafficCounter.cumulativeWrittenBytes(), trafficCounter.lastWriteThroughput());
    }
    if (trafficCounter.cumulativeReadBytes() > 0) {
      log("Read {} bytes @ {} bytes/s", trafficCounter.cumulativeReadBytes(), trafficCounter.lastReadThroughput());
    }
  }

  private void log(String message, Object... objs) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(message, objs);
    }
  }
}
