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

package com.adobe.datum.example.serializable.server;

import com.adobe.datum.common.function.DatumSupplier;
import com.adobe.datum.common.function.DatumSupplierUtil;
import com.adobe.datum.example.datamodel.AddressInfo;
import com.adobe.datum.example.datamodel.AddressInfoRequest;
import com.adobe.datum.example.datamodel.ResourceReader;
import com.adobe.datum.server.handler.DownloadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * @author Adobe Systems Inc.
 */
public final class AddressInfoDownloadHandler implements DownloadHandler<AddressInfoRequest, AddressInfo> {

  private static final Logger LOG = LoggerFactory.getLogger(AddressInfoDownloadHandler.class);

  @Override
  public DatumSupplier<Optional<AddressInfo>> handleRequest(AddressInfoRequest request) {
    int count = request.getCount();
    LOG.info("handling address download request for {} addresses", count);
    try {
      return DatumSupplierUtil.limit(ResourceReader.getAddressInfoSupplier(), count);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("unable to read resource file", e);
    }
  }
}
