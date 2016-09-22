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

import com.adobe.datum.client.DatumClientException;
import com.adobe.datum.common.function.DatumCallback;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import static com.adobe.datum.example.protobuf.api.ExampleProto.AddressProto;
import static com.adobe.datum.example.protobuf.api.ExampleProto.PersonAddressProto;
import static com.adobe.datum.example.protobuf.api.ExampleProto.PersonProto;

/**
 * @author Adobe Systems Inc.
 */
public final class ExampleClientDriver {

  private static final ExampleClient CLIENT = new ExampleClient("0.0.0.0");

  public static void main(String[] args) throws DatumClientException {
    List<PersonProto> personList = CLIENT.downloadPersonInfo("at");
    List<AddressProto> addressList = CLIENT.downloadAddressInfo(personList.size());

    Random rand = new Random();
    List<PersonAddressProto> personAddressMapping =
        personList.stream()
                  .map(person -> buildPersonAddressProto(person, addressList.remove(rand.nextInt(addressList.size()))))
                  .collect(Collectors.toList());
    CLIENT.uploadPersonAddressInfo(personAddressMapping, new DatumCallback() {
      @Override
      public void onComplete() {
        CLIENT.shutdown();
      }
    });
  }

  private static PersonAddressProto buildPersonAddressProto(PersonProto personProto, AddressProto addressProto) {
    return PersonAddressProto.newBuilder()
                             .setPerson(personProto)
                             .setAddress(addressProto)
                             .build();
  }
}
