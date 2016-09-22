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

package com.adobe.datum.client.fixture

import com.adobe.datum.common.io.DataReader
import com.adobe.datum.common.io.DataWriter
import com.adobe.datum.common.io.DatumExternalizable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor

/**
 * @author Adobe Systems Inc.
 */
@TupleConstructor
@ToString
@EqualsAndHashCode
class SerializableFixture implements DatumExternalizable {

  int id
  String name

  @Override
  DatumExternalizable serialize(DataWriter writer) {
    writer.write(id)
    writer.write(name)
    this
  }

  @Override
  DatumExternalizable deserialize(DataReader reader) {
    id = reader.readInteger()
    name = reader.readString()
    this
  }
}
