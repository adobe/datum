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

package com.adobe.datum.example.protobuf.client;

import com.adobe.datum.client.BlockingDatumClient;
import com.adobe.datum.client.DatumClientException;
import com.adobe.datum.common.channel.ConnectionSettings;
import com.adobe.datum.common.function.DatumCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.List;
import static com.adobe.datum.example.protobuf.api.ExampleProto.AddressDownloadRequestProto;
import static com.adobe.datum.example.protobuf.api.ExampleProto.AddressProto;
import static com.adobe.datum.example.protobuf.api.ExampleProto.PersonAddressProto;
import static com.adobe.datum.example.protobuf.api.ExampleProto.PersonAddressUploadRequestProto;
import static com.adobe.datum.example.protobuf.api.ExampleProto.PersonDownloadRequestProto;
import static com.adobe.datum.example.protobuf.api.ExampleProto.PersonProto;

/**
 * @author Adobe Systems Inc.
 */
public final class ExampleClient {

  private static final Logger LOG = LoggerFactory.getLogger(ExampleClient.class);

  private final BlockingDatumClient client;

  ExampleClient(String host) {
    ConnectionSettings connectionSettings = ConnectionSettings.getDefaultSettings();
    connectionSettings.setHost(host);
    client = new BlockingDatumClient(connectionSettings);
  }

  public List<PersonProto> downloadPersonInfo(String query) throws DatumClientException {
    PersonDownloadRequestProto request = PersonDownloadRequestProto.newBuilder()
                                                                   .setQuery(query)
                                                                   .build();
    return client.sendDownloadRequest(request, PersonProto.class, new DatumCallback() {
      @Override
      public void onComplete() {
        LOG.info("person info download complete");
      }

      @Override
      public void onError(Throwable cause) {
        LOG.error("error while receiving person info: {}", cause.getMessage());
      }
    });
  }

  public List<AddressProto> downloadAddressInfo(int count) throws DatumClientException {
    AddressDownloadRequestProto request = AddressDownloadRequestProto.newBuilder()
                                                                     .setCount(count)
                                                                     .build();
    return client.sendDownloadRequest(request, AddressProto.class, new DatumCallback() {
      @Override
      public void onComplete() {
        LOG.info("address upload complete");
      }

      @Override
      public void onError(Throwable cause) {
        LOG.error("error while sending address info: {}", cause.getMessage());
      }
    });
  }

  public void uploadPersonAddressInfo(Collection<PersonAddressProto> collection, DatumCallback callback)
      throws DatumClientException {
    PersonAddressUploadRequestProto request = PersonAddressUploadRequestProto.newBuilder()
                                                                             .setId("upload-id-1")
                                                                             .build();
    client.sendUploadRequest(request, collection, new DatumCallback() {
      @Override
      public void onComplete() {
        LOG.info("person-address info uploaded");
        callback.onComplete();
      }

      @Override
      public void onError(Throwable cause) {
        LOG.error("error while uploading person-address info", cause);
        callback.onError(cause);
      }
    });
  }

  public void shutdown() {
    client.shutdown();
  }
}
