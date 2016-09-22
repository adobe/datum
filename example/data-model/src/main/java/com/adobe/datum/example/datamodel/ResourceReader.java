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

import com.adobe.datum.common.function.DatumSupplier;
import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Adobe Systems Inc.
 */
public final class ResourceReader {

  private ResourceReader() {
  }

  public static DatumSupplier<Optional<PersonInfo>> getPersonInfoSupplier(String query) throws FileNotFoundException {
    return getPersonInfoSupplier()
        .filter(personInfoOptional -> {
          if (!personInfoOptional.isPresent()) {
            return true;
          }
          PersonInfo personInfo = personInfoOptional.get();
          return personInfo.getName().toLowerCase().contains(query.toLowerCase())
              || personInfo.getEmail().toLowerCase().contains(query.toLowerCase())
              || personInfo.getCompany().toLowerCase().contains(query.toLowerCase());
        });
  }

  public static DatumSupplier<Optional<PersonInfo>> getPersonInfoSupplier() throws FileNotFoundException {
    return getLineSupplier("person_info.csv").transform(lineOptional -> lineOptional.map(PersonInfo::parseFromLine));
  }

  public static DatumSupplier<Optional<AddressInfo>> getAddressInfoSupplier() throws FileNotFoundException {
    return getLineSupplier("address_info.csv").transform(lineOptional -> lineOptional.map(AddressInfo::parseFromLine));
  }

  private static DatumSupplier<Optional<String>> getLineSupplier(String fileName) throws FileNotFoundException {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = classloader.getResourceAsStream(fileName);
    Scanner scanner = new Scanner(inputStream, Charsets.UTF_8.name());
    return new DatumSupplier<Optional<String>>() {
      @Override
      public Optional<String> get() throws Exception {
        if (!scanner.hasNextLine()) {
          return Optional.empty();
        }
        String nextLine = scanner.nextLine();
        while (StringUtils.isBlank(nextLine) && scanner.hasNextLine()) {
          nextLine = scanner.nextLine();
        }
        return StringUtils.isBlank(nextLine) ? Optional.empty() : Optional.of(nextLine);
      }

      @Override
      public void onComplete() {
        scanner.close();
      }

      @Override
      public void onError(Throwable cause) {
        scanner.close();
      }
    };
  }

}
