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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adobe Systems Inc.
 */
public final class UncaughtErrorReporterCallback implements DatumCallback {

  private static final Logger LOG = LoggerFactory.getLogger(UncaughtErrorReporterCallback.class);
  private static final String SHOW_WARN_PROPERTY = "datum.uncaughtErrorWarn.show";
  private static final boolean SHOW_WARN = Boolean.valueOf(System.getProperty(SHOW_WARN_PROPERTY, "true"));

  private final Class<?> parentClass;

  public UncaughtErrorReporterCallback(Class<?> parentClass) {
    this.parentClass = parentClass;
  }

  @Override
  public void onError(Throwable cause) {
    if (SHOW_WARN) {
      LOG.warn("Uncaught exception in {} due to {} (full stack trace follows)\n"
                   + "In certain scenarios this can lead to resource leak of some kind.\n"
                   + "It is advised to provide a custom callback if your request handler should cleanup before exit.\n"
                   + "To turn off this warning use property \"-D{}=false\"\n",
               parentClass, cause.getMessage(), SHOW_WARN_PROPERTY);
    }
    LOG.error("Uncaught exception in " + parentClass, cause);
  }
}
