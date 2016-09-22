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

package com.adobe.datum.common.channel.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Adobe Systems Inc.
 */
public final class ChannelMetadata {

  private static final DatumMetadataMessage STREAM_START_MESSAGE = new DatumMetadataMessage(Integer.MAX_VALUE);
  private static final DatumMetadataMessage STREAM_END_MESSAGE   = new DatumMetadataMessage(Integer.MAX_VALUE - 1);

  private static final List<DatumMetadataMessage> METADATA_MESSAGES = ImmutableList.of(streamStartMessage(),
                                                                                       streamEndMessage());

  private ChannelMetadata() { }

  public static DatumMetadataMessage streamStartMessage() {
    return STREAM_START_MESSAGE;
  }

  public static DatumMetadataMessage streamEndMessage() {
    return STREAM_END_MESSAGE;
  }

  public static Optional<DatumMetadataMessage> fromMessageCode(int messageCode) {
    if (messageCode < Iterables.getLast(METADATA_MESSAGES).getMessageCode()) {
      return Optional.empty();
    }
    return METADATA_MESSAGES.stream()
                            .filter(message -> message.getMessageCode() == messageCode)
                            .findAny();
  }

  public static void writeMetadataMessage(DatumMetadataMessage metadataMessage, ByteBuf buffer) throws IOException {
    int messageCodeLength = CodedOutputStream.computeRawVarint32Size(metadataMessage.getMessageCode());
    buffer.ensureWritable(messageCodeLength);
    CodedOutputStream outputStream = CodedOutputStream.newInstance(new ByteBufOutputStream(buffer), messageCodeLength);
    outputStream.writeRawVarint32(metadataMessage.getMessageCode());
    outputStream.flush();
  }
}
