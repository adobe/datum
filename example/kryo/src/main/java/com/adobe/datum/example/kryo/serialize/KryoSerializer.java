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

import com.adobe.datum.common.serialize.DatumSerializer;
import com.adobe.datum.example.kryo.api.KryoRegistry;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * @author Adobe Systems Inc.
 */
public class KryoSerializer implements DatumSerializer<Object> {

  private static final Logger LOG = LoggerFactory.getLogger(KryoSerializer.class);

  private static final int INITIAL_BUFFER_SIZE = 1024; // 1KB
  private static final int MAX_BUFFER_SIZE = 1024 * 1024 * 16; // 16MB

  @Override
  public byte[] serialize(Object item) throws IOException {
    try {
      ByteBufferOutput output = new ByteBufferOutput(INITIAL_BUFFER_SIZE, MAX_BUFFER_SIZE);
      KryoRegistry.instance().writeObject(output, item);
      return output.toBytes();
    } catch (Throwable t) {
      throw new IOException("unable to serialize item: " + item, t);
    }
  }
}
