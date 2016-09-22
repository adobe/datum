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

package com.adobe.datum.common.serialize;

import org.apache.commons.lang3.SerializationUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Adobe Systems Inc.
 * @param <T> Deserialize byte array to this {@link Serializable} type.
 */
public final class SerializableDeserializer<T extends Serializable> implements DatumDeserializer<T> {

  private static final SerializableDeserializer INSTANCE = new SerializableDeserializer();

  private SerializableDeserializer() { }

  public static SerializableDeserializer getInstance() {
    return INSTANCE;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T deserialize(byte[] array, int offset, int length) throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(array, offset, length);
    return (T) SerializationUtils.deserialize(inputStream);
  }
}
