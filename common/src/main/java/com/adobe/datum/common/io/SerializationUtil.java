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

import com.adobe.datum.common.util.Nullables;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Adobe Systems Inc.
 */
public final class SerializationUtil {

  private static final byte[] EMPTY_BUFFER = new byte[0];

  public static void write(DataOutput out, Boolean value) {
    wrapWriteOperation(value, false, out::writeBoolean);
  }

  public static void write(DataOutput out, Long value) {
    wrapWriteOperation(value, 0L, out::writeLong);
  }

  public static void write(DataOutput out, Integer value) {
    wrapWriteOperation(value, 0, out::writeInt);
  }

  public static void write(DataOutput out, Double value) {
    wrapWriteOperation(value, 0D, out::writeDouble);
  }

  public static void write(DataOutput out, BigDecimal value) {
    BigDecimal safeValue = Nullables.getOrDefault(value, BigDecimal.ZERO);
    write(out, safeValue.unscaledValue());
    write(out, safeValue.scale());
  }

  public static void write(DataOutput out, BigInteger value) {
    wrapWriteOperation(value, BigInteger.ZERO, bigInteger -> write(out, bigInteger.toByteArray()));
  }

  public static void write(DataOutput out, String value) {
    wrapWriteOperation(value, StringUtils.EMPTY, (String str) -> write(out, str.getBytes(Charsets.UTF_16)));
  }

  public static void write(DataOutput out, byte[] value) {
    wrapWriteOperation(value, EMPTY_BUFFER, bytes -> {
      out.writeInt(bytes.length);
      out.write(bytes);
    });
  }

  public static void write(DataOutput out, DatumExternalizable obj) {
    obj.serialize(new DataWriter(out));
  }

  public static <T> void write(DataOutput out, Optional<T> value, Consumer<T> consumer) {
    Optional<T> safeValue = Nullables.getOrDefault(value, Optional.<T>empty());
    write(out, safeValue.isPresent());
    safeValue.ifPresent(consumer);
  }

  public static <T extends DatumExternalizable> void write(DataOutput out, Collection<T> collection) {
    write(out, collection, item -> write(out, item));
  }

  public static <T> void write(DataOutput out, Collection<T> collection, Consumer<T> consumer) {
    Collection<T> safeCollection = Nullables.getOrDefault(collection, Collections.<T>emptyList());
    write(out, safeCollection.size());
    safeCollection.forEach(consumer::accept);
  }

  public static <K, V extends DatumExternalizable> void write(DataOutput out, Map<K, V> map, Consumer<K> keyConsumer) {
    write(out, map, keyConsumer, (value) -> write(out, value));
  }

  public static <K, V> void write(DataOutput out, Map<K, V> map, Consumer<K> keyConsumer, Consumer<V> valueConsumer) {
    Map<K, V> safeMap = Nullables.getOrDefault(map, Collections.<K, V>emptyMap());
    write(out, safeMap.size());
    safeMap.entrySet().forEach(entry -> {
      keyConsumer.accept(entry.getKey());
      valueConsumer.accept(entry.getValue());
    });
  }

  private static <T> void wrapWriteOperation(T value, T defaultValue, Callback<T> callback) {
    try {
      callback.call(Nullables.getOrDefault(value, defaultValue));
    } catch (IOException e) {
      throw new RuntimeSerializationException("unable to serialize", e);
    }
  }

  public static Boolean readBoolean(DataInput in) {
    return wrapReadOperation(in::readBoolean);
  }

  public static Long readLong(DataInput in) {
    return wrapReadOperation(in::readLong);
  }

  public static Integer readInteger(DataInput in) {
    return wrapReadOperation(in::readInt);
  }

  public static Double readDouble(DataInput in) {
    return wrapReadOperation(in::readDouble);
  }

  public static String readString(DataInput in) {
    return wrapReadOperation(() -> {
      int size = in.readInt();
      byte[] buffer = new byte[size];
      in.readFully(buffer);
      return new String(buffer, Charsets.UTF_16);
    });
  }

  public static byte[] readBytes(DataInput in) {
    return wrapReadOperation(() -> {
      byte[] buffer = new byte[in.readInt()];
      in.readFully(buffer);
      return buffer;
    });
  }

  public static BigDecimal readBigDecimal(DataInput in) {
    return new BigDecimal(readBigInteger(in), readInteger(in));
  }

  public static BigInteger readBigInteger(DataInput in) {
    return new BigInteger(readBytes(in));
  }

  public static <T> Optional<T> readOptional(DataInput in, Supplier<T> supplier) {
    if (readBoolean(in)) {
      return Optional.of(supplier.get());
    } else {
      return Optional.empty();
    }
  }

  public static <K, V> Map<K, V> readMap(DataInput in, Supplier<K> keySupplier, Supplier<V> valueSupplier) {
    return readSet(in, () -> Maps.immutableEntry(keySupplier.get(), valueSupplier.get()))
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public static <T> Set<T> readSet(DataInput in, Supplier<T> valueSupplier) {
    return Sets.newHashSet(readList(in, valueSupplier));
  }

  public static <T extends Comparable> Set<T> readSortedSet(DataInput in, Supplier<T> valueSupplier) {
    return Sets.newTreeSet(readList(in, valueSupplier));
  }

  public static <T> List<T> readList(DataInput in, Supplier<T> valueSupplier) {
    int size = readInteger(in);
    List<T> list = new ArrayList<>(size);
    while (size-- > 0) {
      list.add(valueSupplier.get());
    }
    return list;
  }

  private static <T> T wrapReadOperation(Callable<T> operation) {
    try {
      return operation.call();
    } catch (Exception e) {
      throw new RuntimeSerializationException("unable to deserialize", e);
    }
  }

  private interface Callback<T> {
    void call(T value) throws IOException;
  }
}
