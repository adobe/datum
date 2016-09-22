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
import java.io.Serializable;

/**
 * @author Adobe Systems Inc.
 */
public final class SerializerProvider {

  private final SerializationContext serializationContext;

  public SerializerProvider(SerializationContext serializationContext) {
    this.serializationContext = serializationContext;
  }

  public SerializationContext getSerializationContext() {
    return serializationContext;
  }

  public <T> DatumSerializer<T> getSerializer(Class<T> clazz) {
    if (serializationContext.isRegisteredSerializer(clazz)) {
      return serializationContext.getSerializer(clazz);
    }
    DatumSerializer<T> serializer = buildDatumSerializer(clazz);
    serializationContext.register(clazz, serializer);
    return serializer;
  }

  public <T> DatumDeserializer<T> getDeserializer(Class<T> clazz) {
    if (serializationContext.isRegisteredDeserializer(clazz)) {
      return serializationContext.getDeserializer(clazz);
    }
    DatumDeserializer<T> deserializer = buildDatumDeserializer(clazz);
    serializationContext.register(clazz, deserializer);
    return deserializer;
  }

  @SuppressWarnings("unchecked")
  private <T> DatumSerializer<T> buildDatumSerializer(Class<T> clazz) {
    if (Message.class.isAssignableFrom(clazz)) {
      return ProtoSerializer.getInstance();
    } else if (Serializable.class.isAssignableFrom(clazz)) {
      return SerializableSerializer.getInstance();
    } else {
      throw new SerializationRuntimeException("Serializer not found for class " + clazz);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> DatumDeserializer<T> buildDatumDeserializer(Class<T> clazz) {
    if (Message.class.isAssignableFrom(clazz)) {
      return new ProtoDeserializer(clazz);
    } else if (Serializable.class.isAssignableFrom(clazz)) {
      return SerializableDeserializer.getInstance();
    } else {
      throw new SerializationRuntimeException("Deserializer not found for class " + clazz);
    }
  }
}
