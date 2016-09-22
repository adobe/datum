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

package com.adobe.datum.example.kryo.serialize;

import com.adobe.datum.common.serialize.DatumDeserializer;
import com.adobe.datum.example.kryo.api.KryoRegistry;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import java.io.ByteArrayInputStream;

/**
 * @author Adobe Systems Inc.
 * @param <T> Deserialize byte array using {@link com.esotericsoftware.kryo.Kryo} deserializer.
 */
public class KryoDeserializer<T> implements DatumDeserializer<T> {

  private final Class<T> clazz;

  public KryoDeserializer(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public T deserialize(byte[] array, int offset, int length) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(array, offset, length);
    return KryoRegistry.instance().readObject(new ByteBufferInput(inputStream), clazz);
  }
}
