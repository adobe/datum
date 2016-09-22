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

package com.adobe.datum.example.kryo.client;

import com.adobe.datum.client.DatumClientException;
import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.common.function.DatumSupplier;
import com.adobe.datum.example.datamodel.AddressInfo;
import com.adobe.datum.example.datamodel.PersonAddressInfo;
import com.adobe.datum.example.datamodel.PersonInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Adobe Systems Inc.
 */
public final class ExampleClientDriver {

  private static final Logger LOG = LoggerFactory.getLogger(ExampleClientDriver.class);

  private static final ExampleClient CLIENT = new ExampleClient("0.0.0.0");

  public static void main(String[] args) throws DatumClientException {
    List<PersonInfo> personList = new ArrayList<>();
    CLIENT.downloadPersonInfo("ale", personList::add, new DatumCallback() {
      @Override
      public void onComplete() {
        getAddressInfo(personList);
      }
    });
  }

  private static void getAddressInfo(final List<PersonInfo> personList) {
    List<AddressInfo> addressList = new ArrayList<>();
    try {
      CLIENT.downloadAddressInfo(personList.size(), addressList::add, new DatumCallback() {
        @Override
        public void onComplete() {
          mapPersonToAddressAndUpload(personList, addressList);
        }
      });
    } catch (Exception e) {
      LOG.error("unable to get address info", e);
    }
  }

  private static void mapPersonToAddressAndUpload(List<PersonInfo> personList, List<AddressInfo> addressList) {
    Random rand = new Random();
    List<PersonAddressInfo> personAddressMapping =
        personList.stream()
                  .map(person -> new PersonAddressInfo(person, addressList.remove(rand.nextInt(addressList.size()))))
                  .collect(Collectors.toList());
    try {
      CLIENT.uploadPersonAddressInfo(DatumSupplier.of(personAddressMapping.iterator()), new DatumCallback() {
        @Override
        public void onComplete() {
          CLIENT.shutdown();
        }
      });
    } catch (Exception e) {
      LOG.error("unable to upload person-address mapping", e);
    }
  }
}
