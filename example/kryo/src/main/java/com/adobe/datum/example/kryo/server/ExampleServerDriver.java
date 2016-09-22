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

package com.adobe.datum.example.kryo.server;

import com.adobe.datum.common.channel.ConnectionSettings;
import com.adobe.datum.example.datamodel.AddressInfo;
import com.adobe.datum.example.datamodel.AddressInfoRequest;
import com.adobe.datum.example.datamodel.PersonAddressInfo;
import com.adobe.datum.example.datamodel.PersonAddressRequest;
import com.adobe.datum.example.datamodel.PersonInfo;
import com.adobe.datum.example.datamodel.PersonInfoRequest;
import com.adobe.datum.example.kryo.serialize.KryoDeserializer;
import com.adobe.datum.example.kryo.serialize.KryoSerializer;
import com.adobe.datum.server.DatumServer;
import java.util.Arrays;

/**
 * @author Adobe Systems Inc.
 */
public final class ExampleServerDriver {

  public static void main(String[] args) throws InterruptedException {
    new DatumServer().addSerializer(new KryoSerializer(), Arrays.asList(PersonInfo.class, AddressInfo.class))
                     .addDeserializer(new KryoDeserializer<>(PersonInfoRequest.class), PersonInfoRequest.class)
                     .addDeserializer(new KryoDeserializer<>(AddressInfoRequest.class), AddressInfoRequest.class)
                     .addDeserializer(new KryoDeserializer<>(PersonAddressRequest.class), PersonAddressRequest.class)
                     .addDeserializer(new KryoDeserializer<>(PersonAddressInfo.class), PersonAddressInfo.class)
                     .addDownloadHandler(new PersonInfoDownloadHandler())
                     .addDownloadHandler(new AddressInfoDownloadHandler())
                     .addUploadHandler(new PersonAddressInfoUploadHandler())
                     .start(ConnectionSettings.getDefaultSettings(), true);
  }
}
