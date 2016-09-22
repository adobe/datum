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

package com.adobe.datum.example.kryo.api;

import com.adobe.datum.example.datamodel.AddressInfo;
import com.adobe.datum.example.datamodel.AddressInfoRequest;
import com.adobe.datum.example.datamodel.PersonAddressInfo;
import com.adobe.datum.example.datamodel.PersonAddressRequest;
import com.adobe.datum.example.datamodel.PersonInfo;
import com.adobe.datum.example.datamodel.PersonInfoRequest;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;
import org.joda.time.DateTime;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author Adobe Systems Inc.
 */
public final class KryoRegistry {

  private static final KryoRegistry INSTANCE = new KryoRegistry();

  private final Kryo kryo = new Kryo();

  private KryoRegistry() {
    Arrays.<Class>asList(PersonInfo.class,
                         AddressInfo.class,
                         PersonAddressInfo.class,
                         PersonInfoRequest.class,
                         AddressInfoRequest.class,
                         PersonAddressRequest.class)
        .forEach(kryo::register);
    kryo.register(DateTime.class, new JodaDateTimeSerializer());
    kryo.register(BigInteger.class, new DefaultSerializers.BigIntegerSerializer());
  }

  public static Kryo instance() {
    return INSTANCE.kryo;
  }
}
