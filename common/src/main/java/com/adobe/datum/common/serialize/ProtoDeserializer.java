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

import com.adobe.datum.common.util.DatumUtil;
import com.adobe.datum.common.util.ProtoUtil;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import java.io.IOException;

/**
 * @author Adobe Systems Inc.
 * @param <T> Deserialize byte array to this {@link Message} type.
 */
public final class ProtoDeserializer<T extends Message> implements DatumDeserializer<T> {

  private final Parser<T> parser;

  public ProtoDeserializer(T defaultInstance) {
    parser = ProtoUtil.getParser(defaultInstance);
  }

  public ProtoDeserializer(Class<T> messageClass) {
    this(DatumUtil.getDefaultInstance(messageClass));
  }

  @Override
  public T deserialize(byte[] array, int offset, int length) throws IOException {
    return parser.parseFrom(array, offset, length);
  }
}
