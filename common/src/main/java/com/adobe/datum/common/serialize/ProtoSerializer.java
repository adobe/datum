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

import com.google.protobuf.Message;

/**
 * @author Adobe Systems Inc.
 * @param <T> Serialize an object of type {@link Message} to a byte array.
 */
public final class ProtoSerializer<T extends Message> implements DatumSerializer<T> {

  private static final ProtoSerializer INSTANCE = new ProtoSerializer();

  private ProtoSerializer() { }

  public static ProtoSerializer getInstance() {
    return INSTANCE;
  }

  @Override
  public byte[] serialize(T message) {
    return message.toByteArray();
  }
}
