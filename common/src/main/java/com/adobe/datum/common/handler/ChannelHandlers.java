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

import com.adobe.datum.common.channel.ConnectionSettings;
import com.adobe.datum.common.serialize.DatumDeserializer;
import com.adobe.datum.common.serialize.ProtoDeserializer;
import com.adobe.datum.common.serialize.ProtoSerializer;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import static com.adobe.datum.common.api.DatumProto.DatumRequestProto;

/**
 * @author Adobe Systems Inc.
 */
public final class ChannelHandlers {

  private static final DatumLoggingHandler        LOGGING_HANDLER    = new DatumLoggingHandler();
  private static final ChannelHandler             FRAME_ENCODER      = new ProtobufVarint32LengthFieldPrepender();
  private static final DatumStreamMetadataEncoder METADATA_ENCODER   = new DatumStreamMetadataEncoder();
  private static final StreamEndHandler           STREAM_END_HANDLER = new StreamEndHandler();

  @SuppressWarnings("unchecked")
  public static final DatumMessageDecoder<DatumRequestProto> REQUEST_DECODER =
      DatumMessageDecoder.of(DatumRequestProto.class, new ProtoDeserializer(DatumRequestProto.getDefaultInstance()));

  @SuppressWarnings("unchecked")
  public static final DatumMessageEncoder<DatumRequestProto> REQUEST_ENCODER =
      DatumMessageEncoder.of(DatumRequestProto.class, ProtoSerializer.getInstance());

  private ChannelHandlers() { }

  public static ChannelHandler loggingHandler() {
    return LOGGING_HANDLER;
  }

  public static ChannelHandler metadataEncoder() {
    return METADATA_ENCODER;
  }

  public static ChannelHandler frameDecoder() {
    return new DatumFrameDecoder();
  }

  public static ChannelHandler frameEncoder() {
    return FRAME_ENCODER;
  }

  public static ChannelHandler requestDecoder() {
    return REQUEST_DECODER;
  }

  public static ChannelHandler requestEncoder() {
    return REQUEST_ENCODER;
  }

  public static ChannelHandler streamEndHandler() {
    return STREAM_END_HANDLER;
  }

  public static <T> ChannelHandler[] inboundHandlers(Class<T> payloadClass,
                                                     DatumDeserializer<T> deserializer,
                                                     ChannelHandler inboundHandler) {
    return new ChannelHandler[] {
        DatumMessageDecoder.of(payloadClass, deserializer),
        inboundHandler,
        streamEndHandler()
    };
  }

  public static ChannelHandler[] trafficShapingHandlers(ConnectionSettings settings) {
    return new ChannelHandler[] {
        new ChannelTrafficShapingHandler(settings.getWriteBandwidth(),
                                         settings.getReadBandwidth(),
                                         settings.getBandwidthCheckInterval(),
                                         settings.getBandwidthWaitDelay()),
        new TrafficLoggingHandler()
    };
  }
}
