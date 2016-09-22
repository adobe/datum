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

/**
 * @author Adobe Systems Inc.
 */
public final class DatumCallbackUtil {

  private DatumCallbackUtil() { }

  public static DatumCallback merge(DatumCallback callback1, DatumCallback callback2) {
    return new MergedDatumCallback(callback1, callback2);
  }

  private static class MergedDatumCallback implements DatumCallback {
    private final DatumCallback callback1;
    private final DatumCallback callback2;

    public MergedDatumCallback(DatumCallback callback1, DatumCallback callback2) {
      this.callback1 = callback1;
      this.callback2 = callback2;
    }

    @Override
    public void onComplete() {
      callback1.onComplete();
      callback2.onComplete();
    }

    @Override
    public void onError(Throwable cause) {
      callback1.onError(cause);
      callback2.onError(cause);
    }
  }
}
