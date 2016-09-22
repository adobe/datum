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

import java.io.DataInput;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Adobe Systems Inc.
 */
public final class DataReader {

  private final DataInput in;

  public DataReader(DataInput in) {
    this.in = in;
  }

  public Boolean readBoolean() {
    return SerializationUtil.readBoolean(in);
  }

  public Long readLong() {
    return SerializationUtil.readLong(in);
  }

  public Integer readInteger() {
    return SerializationUtil.readInteger(in);
  }

  public Double readDouble() {
    return SerializationUtil.readDouble(in);
  }

  public String readString() {
    return SerializationUtil.readString(in);
  }

  public byte[] readBytes() {
    return SerializationUtil.readBytes(in);
  }

  public BigDecimal readBigDecimal() {
    return SerializationUtil.readBigDecimal(in);
  }

  public BigInteger readBigInteger() {
    return SerializationUtil.readBigInteger(in);
  }

  public <T> Optional<T> readOptional(Supplier<T> supplier) {
    return SerializationUtil.readOptional(in, supplier);
  }

  public <K, V> Map<K, V> readMap(Supplier<K> keySupplier, Supplier<V> valueSupplier) {
    return SerializationUtil.readMap(in, keySupplier, valueSupplier);
  }

  public <T> Set<T> readSet(Supplier<T> valueSupplier) {
    return SerializationUtil.readSet(in, valueSupplier);
  }

  public <T extends Comparable> Set<T> readSortedSet(Supplier<T> valueSupplier) {
    return SerializationUtil.readSortedSet(in, valueSupplier);
  }

  public <T> List<T> readList(Supplier<T> valueSupplier) {
    return SerializationUtil.readList(in, valueSupplier);
  }
}
