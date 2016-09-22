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

package com.adobe.datum.common.util;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;

/**
 * @author Adobe Systems Inc.
 */
public final class ProtoUtil {

  private static final String PROTOTYPE_METHOD_NAME = "getDefaultInstance";

  @SuppressWarnings("unchecked")
  public static <T> T getDefaultInstance(Class<T> clazz) throws Exception {
    return (T) clazz.getDeclaredMethod(PROTOTYPE_METHOD_NAME).invoke(clazz);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Message> Parser<T> getParser(T defaultInstance) {
    return (Parser<T>) defaultInstance.getParserForType();
  }
}
