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

package com.adobe.datum.client.handler

import com.adobe.datum.client.fixture.SerializableFixture
import com.adobe.datum.common.channel.metadata.ChannelMetadata
import com.adobe.datum.common.function.DatumCallback
import com.adobe.datum.common.handler.ChannelHandlers
import com.adobe.datum.common.handler.DatumFrameDecoder
import com.adobe.datum.common.handler.DatumMessageDecoder
import com.adobe.datum.common.serialize.SerializableDeserializer
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

/**
 * @author Adobe Systems Inc.
 */
class StreamStartHandlerSpec extends Specification {

  def 'stream start and initial messages should be parsed separately'() {
    given:
      EmbeddedChannel channel = null
      DatumCallback callback =
          new DatumCallback() {
            @Override
            void onComplete() {
              def pipeline = channel.pipeline()
              def streamStartHandler = pipeline.get(StreamStartHandler)
              pipeline.addAfter(streamStartHandler.baseName,
                                'test-decoder',
                                DatumMessageDecoder.of(SerializableFixture, SerializableDeserializer.instance))
            }
          }

      def fixture1 = new SerializableFixture(123, 'test-fixture-1')
      def fixture2 = new SerializableFixture(456, 'test-fixture-2')
      ByteBuf buffer = serializeFixtures(fixture1, fixture2)

    when:
      channel = new EmbeddedChannel(ChannelHandlers.frameDecoder(), new StreamStartHandler(callback))

    then:
      [DatumFrameDecoder, StreamStartHandler] ==
          channel.pipeline().findAll { it.key.indexOf('EmbeddedChannel') < 0 }.collect { it.value.class }

    when:
      channel.writeInbound(buffer.retain())
      channel.finish()

    then:
      channel.readInbound() == fixture1
      channel.readInbound() == fixture2
  }

  private static ByteBuf serializeFixtures(SerializableFixture... fixtures) {
    ByteBuf buffer = Unpooled.buffer()
    ChannelMetadata.writeMetadataMessage(ChannelMetadata.streamStartMessage(), buffer)

    fixtures.each { fixture ->
      def serialized = SerializationUtils.serialize(fixture)
      buffer.writeByte(serialized.length)
      serialized.eachByte { buffer.writeByte(it as int) }
    }

    buffer.duplicate()
  }
}
