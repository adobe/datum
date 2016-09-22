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
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Adobe Systems Inc.
 * @param <T> Type that this consumer accepts.
 */
public interface DatumConsumer<T> extends DatumCallback {

  void accept(T item) throws Exception;

  default Consumer<T> toJavaConsumer() {
    return item -> {
      try {
        DatumConsumer.this.accept(item);
      } catch (Exception e) {
        throw new WrappedRuntimeException("unable to accept item " + item, e);
      }
    };
  }

  default <U> DatumConsumer<U> transform(Function<U, T> transformer) {
    Preconditions.checkNotNull(transformer, "transformer cannot be null");
    return item -> DatumConsumer.this.accept(transformer.apply(item));
  }
}
