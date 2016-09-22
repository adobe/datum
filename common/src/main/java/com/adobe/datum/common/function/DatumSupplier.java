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

package com.adobe.datum.common.function;

import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Adobe Systems Inc.
 * @param <T> Type that this supplier can provide.
 */
public interface DatumSupplier<T> extends DatumCallback {

  T get() throws Exception;

  default Supplier<T> toJavaSupplier() {
    return DatumSupplierUtil.toJavaSupplier(this);
  }

  default <U> DatumSupplier<U> transform(Function<T, U> transformer) {
    return DatumSupplierUtil.transform(this, transformer);
  }

  default DatumSupplier<T> filter(Predicate<T> predicate) {
    return DatumSupplierUtil.filter(this, predicate);
  }

  static <T> DatumSupplier<Optional<T>> of(Iterator<T> iterator) {
    return DatumSupplierUtil.from(iterator);
  }

  static <T> DatumSupplier<Optional<T>> of(Iterable<T> iterable) {
    return of(iterable.iterator());
  }

  static <T> DatumSupplier<Optional<T>> of(Optional<T> optional) {
    return of(optional.isPresent() ? Iterators.singletonIterator(optional.get()) : Collections.emptyIterator());
  }

  static <T> DatumSupplier<Optional<T>> of(T item) {
    return of(Iterators.singletonIterator(item));
  }
}
