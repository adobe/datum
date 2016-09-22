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

import com.adobe.datum.common.channel.metadata.DatumMetadataMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adobe Systems Inc.
 */
@ChannelHandler.Sharable
public final class StreamEndHandler extends SimpleChannelInboundHandler<DatumMetadataMessage> {

  private static final Logger LOG = LoggerFactory.getLogger(StreamEndHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext context, DatumMetadataMessage metadataMessage) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("stream end message received [{}]", metadataMessage.getMessageCode());
    }

    // TODO do request id verification and error handling
    context.close();
  }
}
