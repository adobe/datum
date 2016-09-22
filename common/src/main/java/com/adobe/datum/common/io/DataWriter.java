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

package com.adobe.datum.common.io;

import java.io.DataOutput;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Adobe Systems Inc.
 */
public final class DataWriter {

  private final DataOutput out;

  public DataWriter(DataOutput out) {
    this.out = out;
  }

  public void write(Boolean value) {
    SerializationUtil.write(out, value);
  }

  public void write(Long value) {
    SerializationUtil.write(out, value);
  }

  public void write(Integer value) {
    SerializationUtil.write(out, value);
  }

  public void write(Double value) {
    SerializationUtil.write(out, value);
  }

  public void write(BigDecimal value) {
    SerializationUtil.write(out, value);
  }

  public void write(BigInteger value) {
    SerializationUtil.write(out, value);
  }

  public void write(String value) {
    SerializationUtil.write(out, value);
  }

  public <T> void write(Optional<T> value, Consumer<T> consumer) {
    SerializationUtil.write(out, value, consumer);
  }

  public void write(byte[] value) {
    SerializationUtil.write(out, value);
  }

  public <T extends DatumExternalizable> void write(Collection<T> collection) {
    SerializationUtil.write(out, collection);
  }

  public <T> void write(Collection<T> collection, Consumer<T> consumer) {
    SerializationUtil.write(out, collection, consumer);
  }

  public <K, V extends DatumExternalizable> void write(Map<K, V> map, Consumer<K> keyConsumer) {
    SerializationUtil.write(out, map, keyConsumer);
  }

  public <K, V> void write(Map<K, V> map, Consumer<K> keyConsumer, Consumer<V> valueConsumer) {
    SerializationUtil.write(out, map, keyConsumer, valueConsumer);
  }
}
