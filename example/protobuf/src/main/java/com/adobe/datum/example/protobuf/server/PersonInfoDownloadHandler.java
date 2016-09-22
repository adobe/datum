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

import com.adobe.datum.common.function.DatumSupplier;
import com.adobe.datum.example.datamodel.PersonInfo;
import com.adobe.datum.example.datamodel.ResourceReader;
import com.adobe.datum.server.handler.DownloadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.function.Function;
import static com.adobe.datum.example.protobuf.api.ExampleProto.PersonDownloadRequestProto;
import static com.adobe.datum.example.protobuf.api.ExampleProto.PersonProto;

/**
 * @author Adobe Systems Inc.
 */
public final class PersonInfoDownloadHandler implements DownloadHandler<PersonDownloadRequestProto, PersonProto> {

  private static final Logger LOG = LoggerFactory.getLogger(AddressInfoDownloadHandler.class);

  public static final Function<PersonInfo, PersonProto> PERSON_TRANSFORMER =
      personInfo ->
          PersonProto.newBuilder()
                     .setId(personInfo.getId())
                     .setName(personInfo.getName())
                     .setPhoneNumber(personInfo.getPhoneNumber())
                     .setEmail(personInfo.getEmail())
                     .setLastLogin(personInfo.getLastLoginString())
                     .setCompany(personInfo.getCompany())
                     .setCompanyId(personInfo.getCompanyId().toString())
                     .build();

  @Override
  public DatumSupplier<Optional<PersonProto>> handleRequest(PersonDownloadRequestProto request) {
    String query = request.getQuery();
    LOG.info("handling person info download request for query: {}", query);
    try {
      return ResourceReader.getPersonInfoSupplier()
                           .filter(personInfoOptional -> {
                             if (!personInfoOptional.isPresent()) {
                               return true;
                             }
                             PersonInfo personInfo = personInfoOptional.get();
                             return personInfo.getName().toLowerCase().contains(query.toLowerCase())
                                 || personInfo.getEmail().toLowerCase().contains(query.toLowerCase())
                                 || personInfo.getCompany().toLowerCase().contains(query.toLowerCase());
                           })
                           .transform(personInfo -> personInfo.map(PERSON_TRANSFORMER));
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("unable to read resource file", e);
    }
  }
}
