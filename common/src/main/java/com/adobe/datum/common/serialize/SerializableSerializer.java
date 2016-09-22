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
import java.io.Serializable;

/**
 * @author Adobe Systems Inc.
 * @param <T> Serialize an object of type {@link Serializable} to byte array.
 */
public final class SerializableSerializer<T extends Serializable> implements DatumSerializer<T> {

  private static final SerializableSerializer INSTANCE = new SerializableSerializer();

  private SerializableSerializer() { }

  public static SerializableSerializer getInstance() {
    return INSTANCE;
  }

  @Override
  public byte[] serialize(T item) {
    return SerializationUtils.serialize(item);
  }
}
