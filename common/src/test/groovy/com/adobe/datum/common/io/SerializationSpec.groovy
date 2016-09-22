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

package com.adobe.datum.common.io

import com.adobe.datum.common.fixture.TestContent
import com.adobe.datum.common.fixture.TestItem
import com.adobe.datum.common.fixture.TestTag
import com.google.common.io.ByteStreams
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * @author Adobe Systems Inc.
 */
class SerializationSpec extends Specification {

  def 'can serialize empty test item'() {
    given:
      TestItem item = TestItem.builder().build()
      def out = ByteStreams.newDataOutput()

    when:
      SerializationUtil.write(out, item)
      def deserialized = SerializationUtils.deserialize(SerializationUtils.serialize(item)) as TestItem

    then:
      deserialized.id == 0
      deserialized.name == ''
      deserialized.value == 0 as BigInteger
      deserialized.count == 0
      deserialized.margin == 0 as BigDecimal
      !deserialized.isDocumented
      deserialized.price == 0.0D
      deserialized.tags == Optional.empty()
      deserialized.timestamps == []
      deserialized.headers == [:]
      deserialized.contents == [:]
      deserialized.scores == [] as TreeSet
  }

  def 'can serialize map of items'() {
    given:
      def itemMap = [
          (1L): TestItem.builder()
                        .id(1)
                        .name('test-item')
                        .value(123456789012345678901234567890 as BigInteger)
                        .count(12345)
                        .margin(new BigDecimal(new BigInteger(1234567890), 10))
                        .isDocumented(true)
                        .price(123.456)
                        .tags(Optional.of([TestTag.builder()
                                                  .id(98765)
                                                  .name('abcd')
                                                  .build(),
                                           TestTag.builder()
                                                  .id(9221)
                                                  .name('xyz')
                                                  .build()] as Set))
                        .timestamps([123455124L, 13523621L])
                        .headers(['head-1': 'val-1',
                                  'head-2': 'val-2'])
                        .contents(['content-1': TestContent.builder()
                                                           .id(4122)
                                                           .body('content-body-1'.bytes)
                                                           .build(),
                                   'content-2': TestContent.builder()
                                                           .id(4123)
                                                           .body('content-body-2'.bytes)
                                                           .build()])
                        .scores([6, 5, 4, 3, 2, 1] as TreeSet)
                        .build()
      ]
      def out = ByteStreams.newDataOutput()
      def writer = new DataWriter(out)

    when:
      writer.write(itemMap, new Consumer<Long>() {
        @Override
        void accept(Long key) {
          writer.write(key)
        }
      })

      def reader = new DataReader(ByteStreams.newDataInput(out.toByteArray()))
      Map<Long, TestItem> deserialized =
          reader.readMap(new Supplier<Long>() {
            @Override
            Long get() {
              reader.readLong()
            }
          }, new Supplier<TestItem>() {
            @Override
            TestItem get() {
              new TestItem().deserialize(reader)
            }
          })

    then:
      itemMap == deserialized
  }
}
