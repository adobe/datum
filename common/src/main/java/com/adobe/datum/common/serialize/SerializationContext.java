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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * @author Adobe Systems Inc.
 */
public final class SerializationContext {

  private static final Logger LOG = LoggerFactory.getLogger(SerializationContext.class);

  private final Map<Class, DatumSerializer>   serializerMap   = Maps.newHashMap();
  private final Map<Class, DatumDeserializer> deserializerMap = Maps.newHashMap();

  public <T> void register(Class<? extends T> clazz, DatumSerializer<T> serializer) {
    Preconditions.checkArgument(!serializerMap.containsKey(clazz),
                                "a serializer already exists for: " + clazz);
    LOG.debug("registering serializer {} for {}", serializer, clazz);
    serializerMap.put(clazz, serializer);
  }

  public <T> void register(Class<? extends T> clazz, DatumDeserializer<T> deserializer) {
    Preconditions.checkArgument(!deserializerMap.containsKey(clazz),
                                "a serializer already exists for: " + clazz);
    LOG.debug("registering deserializer {} for {}", deserializer, clazz);
    deserializerMap.put(clazz, deserializer);
  }

  @SuppressWarnings("unchecked")
  public <T> DatumSerializer<T> getSerializer(Class<T> clazz) {
    Preconditions.checkArgument(isRegisteredSerializer(clazz), "no serializer found for " + clazz);
    return serializerMap.get(clazz);
  }

  @SuppressWarnings("unchecked")
  public <T> DatumDeserializer<T> getDeserializer(Class<T> clazz) {
    Preconditions.checkArgument(isRegisteredDeserializer(clazz), "no deserializer found for " + clazz);
    return deserializerMap.get(clazz);
  }

  public boolean isRegisteredSerializer(Class<?> clazz) {
    return serializerMap.containsKey(clazz);
  }

  public boolean isRegisteredDeserializer(Class<?> clazz) {
    return deserializerMap.containsKey(clazz);
  }
}
