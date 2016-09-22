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

import org.apache.commons.lang3.StringUtils;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * @author Adobe Systems Inc.
 */
public final class Nullables {

  private Nullables() { }

  public static <T> T getOrDefault(T item, Supplier<T> defaultValueSupplier) {
    return item == null ? defaultValueSupplier.get() : item;
  }

  @Nonnull
  public static <T> T getOrDefault(T item, T defaultValue) {
    return getOrDefault(item, () -> defaultValue);
  }

  @Nonnull
  public static String getOrDefault(String str) {
    return getOrDefault(str, StringUtils.EMPTY);
  }

  public static <T> boolean ifNonNull(T item, Consumer<T> consumer) {
    if (item != null) {
      consumer.accept(item);
      return true;
    }
    return false;
  }

  public static <T, U> boolean ifNonNull(T item, Function<T, U> transformer, Consumer<U> consumer) {
    return item != null && ifNonNull(transformer.apply(item), consumer);
  }
}
