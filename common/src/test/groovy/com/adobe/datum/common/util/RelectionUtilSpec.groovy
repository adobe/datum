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

package com.adobe.datum.common.util

import com.adobe.datum.common.api.DatumProto
import com.adobe.datum.common.channel.metadata.DatumMetadataMessage
import com.adobe.datum.common.fixture.TestUploadHandler
import spock.lang.Specification

/**
 * @author Adobe Systems Inc.
 */
class RelectionUtilSpec extends Specification {

  def 'should be able to get first param type'() {
    expect:
      DatumProto.DatumRequestProto == ReflectionUtil.getFirstTypeParameter(TestUploadHandler)
  }

  def 'should be able to get second param type'() {
    expect:
      DatumMetadataMessage == ReflectionUtil.getSecondTypeParameter(TestUploadHandler)
  }
}
