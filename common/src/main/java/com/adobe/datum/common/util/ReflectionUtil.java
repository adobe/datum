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

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.ClassUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * @author Adobe Systems Inc.
 */
public final class ReflectionUtil {

  private ReflectionUtil() { }

  public static Class getClassByName(String className) throws ClassNotFoundException {
    return ClassUtils.getClass(className, false);
  }

  @SuppressWarnings("unchecked")
  public static <T> T invokeParseFrom(ByteString parameters, Class<T> requestClass)
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    Method parseFromMethod = ClassUtils.getPublicMethod(requestClass, "parseFrom", ByteString.class);
    return (T) parseFromMethod.invoke(requestClass, parameters);
  }

  public static <T> Class<T> getFirstTypeParameter(Class handlerClass) {
    return getTypeParameter(handlerClass, 0);
  }

  public static <T> Class<T> getSecondTypeParameter(Class handlerClass) {
    return getTypeParameter(handlerClass, 1);
  }

  @SuppressWarnings("unchecked")
  private static <T> Class<T> getTypeParameter(Class handlerClass, int index) {
    ParameterizedType parameterizedType = getParameterizedType(handlerClass);
    return (Class<T>) parameterizedType.getActualTypeArguments()[index];
  }

  private static ParameterizedType getParameterizedType(Class handlerClass) {
    // TODO - traverse chain of super classes to find correct type
    return (ParameterizedType) (handlerClass.getGenericInterfaces()[0]);
  }
}
