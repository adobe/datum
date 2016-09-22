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

import com.google.protobuf.ByteString;
import java.io.IOException;

/**
 * @author Adobe Systems Inc.
 * @param <T> Deserialize bytes to this type of object.
 */
public interface DatumDeserializer<T> {

  T deserialize(byte[] array, int offset, int length) throws IOException;

  default T deserialize(ByteString byteString) throws IOException {
    return deserialize(byteString.toByteArray(), 0, byteString.size());
  }
}
