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

package com.adobe.datum.example.json.serialize;

import com.adobe.datum.common.serialize.DatumDeserializer;
import java.io.IOException;

/**
 * @author Adobe Systems Inc.
 * @param <T> Deserialize byte array using a json deserializer.
 */
public class JsonDeserializer<T> implements DatumDeserializer<T> {

  private final Class<T> clazz;

  public JsonDeserializer(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public T deserialize(byte[] array, int offset, int length) throws IOException {
    return JsonSerializationUtil.deserialize(array, offset, length, clazz);
  }
}
