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

import com.adobe.datum.common.serialize.DatumSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

/**
 * @author Adobe Systems Inc.
 * @param <T> Message type that can be serialized into bytes.
 */
@ChannelHandler.Sharable
public final class DatumMessageEncoder<T> extends MessageToMessageEncoder<T> {

  private final DatumSerializer<T> serializer;

  public DatumMessageEncoder(Class<T> payloadClass, DatumSerializer<T> serializer) {
    super(payloadClass);
    this.serializer = serializer;
  }

  @Override
  protected void encode(ChannelHandlerContext context, T payload, List<Object> out) throws Exception {
    ByteBuf content = Unpooled.wrappedBuffer(serializer.serialize(payload));
    out.add(content);
  }

  public static <T> DatumMessageEncoder<T> of(Class<T> clazz, DatumSerializer<T> serializer) {
    return new DatumMessageEncoder<>(clazz, serializer);
  }
}
