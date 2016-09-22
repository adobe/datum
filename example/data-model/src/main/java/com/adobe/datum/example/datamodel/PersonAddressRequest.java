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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Adobe Systems Inc.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonAddressRequest implements DatumExternalizable {

  private long timestamp;

  @Override
  public PersonAddressRequest serialize(DataWriter writer) {
    writer.write(timestamp);
    return this;
  }

  @Override
  public PersonAddressRequest deserialize(DataReader reader) {
    timestamp = reader.readLong();
    return this;
  }
}
