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

import com.adobe.datum.common.function.DatumConsumer;
import com.adobe.datum.example.datamodel.AddressInfo;
import com.adobe.datum.example.datamodel.PersonAddressInfo;
import com.adobe.datum.example.datamodel.PersonAddressRequest;
import com.adobe.datum.example.datamodel.PersonInfo;
import com.adobe.datum.server.handler.UploadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adobe Systems Inc.
 */
public final class PersonAddressInfoUploadHandler implements UploadHandler<PersonAddressRequest, PersonAddressInfo> {

  private static final Logger LOG = LoggerFactory.getLogger(PersonAddressInfoUploadHandler.class);

  @Override
  public DatumConsumer<PersonAddressInfo> handleRequest(PersonAddressRequest request) {
    LOG.info("handling upload request for person-address mapping");
    return personAddress -> {
      PersonInfo person = personAddress.getPersonInfo();
      AddressInfo address = personAddress.getAddressInfo();
      LOG.info("{} lives in {}, {}, {}", person.getName(), address.getCity(), address.getState(), address.getCountry());
    };
  }
}
