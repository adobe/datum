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

import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Adobe Systems Inc.
 */
public final class DatumSupplierUtil {

  private DatumSupplierUtil() { }

  public static <T> Supplier<T> toJavaSupplier(DatumSupplier<T> datumSupplier) {
    return new JavaSupplier<>(datumSupplier);
  }

  public static <T, U> DatumSupplier<U> transform(DatumSupplier<T> datumSupplier, Function<T, U> transformer) {
    Preconditions.checkNotNull(transformer, "transformer cannot be null");
    return new TransformingDatumSupplier<>(datumSupplier, transformer);
  }

  public static <T> DatumSupplier<T> filter(DatumSupplier<T> datumSupplier, Predicate<T> predicate) {
    Preconditions.checkNotNull(predicate, "predicate cannot be null");
    return new FilteredDatumSupplier<>(datumSupplier, predicate);
  }

  public static <T> DatumSupplier<Optional<T>> limit(DatumSupplier<Optional<T>> datumSupplier, int limit) {
    return new LimitingDatumSupplier<>(datumSupplier, limit, Optional.empty());
  }

  public static <T> DatumSupplier<Optional<T>> from(Iterator<T> iterator) {
    return new IteratorDatumSupplier<>(iterator);
  }

  private static final class JavaSupplier<T> implements Supplier<T> {

    private final DatumSupplier<T> delegate;

    private JavaSupplier(DatumSupplier<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public T get() {
      try {
        return delegate.get();
      } catch (Exception e) {
        throw new WrappedRuntimeException("unable to retrieve item", e);
      }
    }
  }

  private abstract static class DatumCallbackWrapper implements DatumCallback {

    private final DatumCallback delegate;

    public DatumCallbackWrapper(DatumCallback delegate) {
      this.delegate = delegate;
    }

    @Override
    public void onComplete() {
      delegate.onComplete();
    }

    @Override
    public void onError(Throwable cause) {
      delegate.onError(cause);
    }
  }

  private static final class TransformingDatumSupplier<T, U> extends DatumCallbackWrapper implements DatumSupplier<U> {

    private final DatumSupplier<T> delegate;
    private final Function<T, U>   transformer;

    private TransformingDatumSupplier(DatumSupplier<T> delegate, Function<T, U> transformer) {
      super(delegate);
      this.delegate = delegate;
      this.transformer = transformer;
    }

    @Override
    public U get() throws Exception {
      return transformer.apply(delegate.get());
    }
  }

  private static final class FilteredDatumSupplier<T> extends DatumCallbackWrapper implements DatumSupplier<T> {

    private final DatumSupplier<T> delegate;
    private final Predicate<T>     predicate;

    private FilteredDatumSupplier(DatumSupplier<T> delegate, Predicate<T> predicate) {
      super(delegate);
      this.delegate = delegate;
      this.predicate = predicate;
    }

    @Override
    public T get() throws Exception {
      T item = delegate.get();
      while (!predicate.test(item)) {
        item = delegate.get();
      }
      return item;
    }
  }

  private static final class LimitingDatumSupplier<T> extends DatumCallbackWrapper implements DatumSupplier<T> {

    private final AtomicInteger counter = new AtomicInteger();

    private final DatumSupplier<T> delegate;
    private final int              limit;
    private final T                defaultValue;

    private LimitingDatumSupplier(DatumSupplier<T> delegate, int limit, T defaultValue) {
      super(delegate);
      this.delegate = delegate;
      this.limit = limit;
      this.defaultValue = defaultValue;
    }

    @Override
    public T get() throws Exception {
      if (counter.get() < limit) {
        counter.incrementAndGet();
        return delegate.get();
      }
      return defaultValue;
    }
  }

  private static final class IteratorDatumSupplier<T> implements DatumSupplier<Optional<T>> {

    private final Iterator<T> iterator;

    private IteratorDatumSupplier(Iterator<T> iterator) {
      this.iterator = iterator;
    }

    @Override
    public Optional<T> get() throws Exception {
      return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }
  }
}
