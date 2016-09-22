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

package com.adobe.datum.common.fixture;

import com.adobe.datum.common.io.DataReader;
import com.adobe.datum.common.io.DataWriter;
import com.adobe.datum.common.io.DatumExternalizable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Adobe Systems Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class TestItem implements DatumExternalizable {

  private long                     id;
  private String                   name;
  private BigInteger               value;
  private Integer                  count;
  private BigDecimal               margin;
  private Boolean                  isDocumented;
  private Double                   price;
  private Optional<Set<TestTag>>   tags;
  private List<Long>               timestamps;
  private Map<String, String>      headers;
  private Map<String, TestContent> contents;
  private Set<Integer>             scores;

  @Override
  public TestItem serialize(DataWriter writer) {
    writer.write(id);
    writer.write(name);
    writer.write(value);
    writer.write(count);
    writer.write(margin);
    writer.write(isDocumented);
    writer.write(price);
    writer.write(tags, writer::write);
    writer.write(timestamps, writer::write);
    writer.write(headers, writer::write, writer::write);
    writer.write(contents, writer::write);
    writer.write(scores, writer::write);
    return this;
  }

  @Override
  public TestItem deserialize(DataReader reader) {
    id = reader.readLong();
    name = reader.readString();
    value = reader.readBigInteger();
    count = reader.readInteger();
    margin = reader.readBigDecimal();
    isDocumented = reader.readBoolean();
    price = reader.readDouble();
    tags = reader.readOptional(() -> reader.readSet(() -> new TestTag().deserialize(reader)));
    timestamps = reader.readList(reader::readLong);
    headers = reader.readMap(reader::readString, reader::readString);
    contents = reader.readMap(reader::readString, () -> new TestContent().deserialize(reader));
    scores = reader.readSortedSet(reader::readInteger);
    return this;
  }
}
