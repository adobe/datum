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

package com.adobe.datum.example.protobuf.server;

import com.adobe.datum.common.channel.ConnectionSettings;
import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.server.DatumServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adobe Systems Inc.
 */
public final class ExampleServerDriver {

  private static final Logger LOG = LoggerFactory.getLogger(ExampleServerDriver.class);

  public static final DatumCallback DOWNLOAD_CALLBACK = new DatumCallback() {
    @Override
    public void onComplete() {
      LOG.info("items sent successfully");
    }

    @Override
    public void onError(Throwable cause) {
      LOG.warn("error while sending items {}", cause.getMessage());
    }
  };

  private static final DatumCallback UPLOAD_CALLBACK = new DatumCallback() {
    @Override
    public void onComplete() {
      LOG.info("mapping result uploaded successfully");
    }

    @Override
    public void onError(Throwable cause) {
      LOG.error("unable to import mapping result due to {}", cause.getMessage());
    }
  };

  public static void main(String[] args) throws InterruptedException {
    new DatumServer().addDownloadHandler(new PersonInfoDownloadHandler(), DOWNLOAD_CALLBACK)
                     .addDownloadHandler(new AddressInfoDownloadHandler(), DOWNLOAD_CALLBACK)
                     .addUploadHandler(new PersonAddressInfoUploadHandler(), UPLOAD_CALLBACK)
                     .start(ConnectionSettings.getDefaultSettings(), true);
  }
}
