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

import com.adobe.datum.common.channel.metadata.ChannelMetadata;
import com.adobe.datum.common.channel.metadata.DatumMetadataMessage;
import com.google.protobuf.CodedInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import java.util.Optional;

/**
 * Based on {@link io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder}.
 * Adds ability to decode special messages based on Integer.MAX_VALUE length.
 *
 * @author Adobe Systems Inc.
 */
public class DatumFrameDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    in.markReaderIndex();
    final byte[] buf = new byte[5];
    for (int i = 0; i < buf.length; i ++) {
      if (!in.isReadable()) {
        in.resetReaderIndex();
        return;
      }

      buf[i] = in.readByte();
      if (buf[i] >= 0) {
        int length = CodedInputStream.newInstance(buf, 0, i + 1).readRawVarint32();
        if (length < 0) {
          throw new CorruptedFrameException("negative length: " + length);
        }

        Optional<DatumMetadataMessage> metadataMessage = ChannelMetadata.fromMessageCode(length);
        if (metadataMessage.isPresent()) {
          out.add(metadataMessage.get());
          return;
        }

        if (in.readableBytes() < length) {
          in.resetReaderIndex();
          return;
        } else {
          out.add(in.readBytes(length));
          return;
        }
      }
    }

    // Couldn't find the byte whose MSB is off.
    throw new CorruptedFrameException("length wider than 32-bit");
  }
}
