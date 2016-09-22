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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Adobe Systems Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class PersonInfo implements DatumExternalizable {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

  private int        id;
  private String     name;
  private String     phoneNumber;
  private String     email;
  private DateTime   lastLogin;
  private String     company;
  private BigInteger companyId;

  public String getLastLoginString() {
    return DATE_FORMATTER.print(lastLogin);
  }

  @Override
  public PersonInfo serialize(DataWriter writer) {
    writer.write(id);
    writer.write(name);
    writer.write(phoneNumber);
    writer.write(email);
    writer.write(getLastLoginString());
    writer.write(company);
    writer.write(companyId);
    return this;
  }

  @Override
  public PersonInfo deserialize(DataReader reader) {
    id = reader.readInteger();
    name = reader.readString();
    phoneNumber = reader.readString();
    email = reader.readString();
    lastLogin = DATE_FORMATTER.parseDateTime(reader.readString());
    company = reader.readString();
    companyId = reader.readBigInteger();
    return this;
  }

  public static PersonInfo parseFromLine(String line) {
    Iterator<String> values = Arrays.asList(line.split("\\|")).iterator();
    PersonInfo personInfo = new PersonInfo();
    personInfo.id = Integer.parseInt(values.next());
    personInfo.name = values.next();
    personInfo.phoneNumber = values.next();
    personInfo.email = values.next();
    personInfo.lastLogin = DATE_FORMATTER.parseDateTime(values.next());
    personInfo.company = values.next();
    personInfo.companyId = new BigInteger(values.next());
    return personInfo;
  }
}
