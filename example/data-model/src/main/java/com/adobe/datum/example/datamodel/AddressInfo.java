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

package com.adobe.datum.example.datamodel;

import com.adobe.datum.common.io.DataReader;
import com.adobe.datum.common.io.DataWriter;
import com.adobe.datum.common.io.DatumExternalizable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Adobe Systems Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class AddressInfo implements DatumExternalizable {

  private int    id;
  private String street;
  private String city;
  private String state;
  private String zip;
  private String country;
  private String coordinates;

  @Override
  public AddressInfo serialize(DataWriter writer) {
    writer.write(id);
    writer.write(street);
    writer.write(city);
    writer.write(state);
    writer.write(zip);
    writer.write(country);
    writer.write(coordinates);
    return this;
  }

  @Override
  public AddressInfo deserialize(DataReader reader) {
    id = reader.readInteger();
    street = reader.readString();
    city = reader.readString();
    state = reader.readString();
    zip = reader.readString();
    country = reader.readString();
    coordinates = reader.readString();
    return this;
  }

  public static AddressInfo parseFromLine(String line) {
    Iterator<String> values = Arrays.asList(line.split("\\|")).iterator();
    AddressInfo addressInfo = new AddressInfo();
    addressInfo.id = Integer.parseInt(values.next());
    addressInfo.street = values.next();
    addressInfo.city = values.next();
    addressInfo.state = values.next();
    addressInfo.zip = values.next();
    addressInfo.country = values.next();
    addressInfo.coordinates = values.next();
    return addressInfo;
  }
}
