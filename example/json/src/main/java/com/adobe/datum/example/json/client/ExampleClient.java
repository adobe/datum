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

package com.adobe.datum.example.json.client;

import com.adobe.datum.client.DatumClient;
import com.adobe.datum.client.DatumClientException;
import com.adobe.datum.common.channel.ConnectionSettings;
import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.common.function.DatumConsumer;
import com.adobe.datum.common.function.DatumSupplier;
import com.adobe.datum.example.json.api.AddressInfoJson;
import com.adobe.datum.example.json.api.AddressInfoRequest;
import com.adobe.datum.example.json.api.PersonAddressInfo;
import com.adobe.datum.example.json.api.PersonAddressRequest;
import com.adobe.datum.example.json.api.PersonInfoJson;
import com.adobe.datum.example.json.api.PersonInfoRequest;
import com.adobe.datum.example.json.serialize.JsonDeserializer;
import com.adobe.datum.example.json.serialize.JsonSerializer;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Adobe Systems Inc.
 */
public final class ExampleClient {

  private final DatumClient client;

  ExampleClient(String host) {
    ConnectionSettings connectionSettings = ConnectionSettings.getDefaultSettings();
    connectionSettings.setHost(host);
    client = new DatumClient(connectionSettings)
        .addDeserializer(new JsonDeserializer<>(PersonInfoJson.class), PersonInfoJson.class)
        .addDeserializer(new JsonDeserializer<>(AddressInfoJson.class), AddressInfoJson.class)
        .addSerializer(new JsonSerializer(), Arrays.asList(PersonInfoRequest.class,
                                                           AddressInfoRequest.class,
                                                           PersonAddressRequest.class,
                                                           PersonAddressInfo.class));
  }

  public void downloadPersonInfo(String query, DatumConsumer<PersonInfoJson> consumer, DatumCallback callback)
      throws DatumClientException {
    PersonInfoRequest request = new PersonInfoRequest(query);
    client.sendDownloadRequest(request, PersonInfoJson.class, consumer, callback);
  }

  public void downloadAddressInfo(int count, DatumConsumer<AddressInfoJson> consumer, DatumCallback callback)
      throws DatumClientException {
    AddressInfoRequest request = new AddressInfoRequest(count);
    client.sendDownloadRequest(request, AddressInfoJson.class, consumer, callback);
  }

  public void uploadPersonAddressInfo(DatumSupplier<Optional<PersonAddressInfo>> supplier, DatumCallback callback)
      throws DatumClientException {
    PersonAddressRequest request = new PersonAddressRequest(System.currentTimeMillis());
    client.sendUploadRequest(request, PersonAddressInfo.class, supplier, callback);
  }

  public void shutdown() {
    client.shutdown();
  }
}
