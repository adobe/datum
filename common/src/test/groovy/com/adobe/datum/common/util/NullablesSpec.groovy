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

import spock.lang.Specification
import java.util.function.Supplier

/**
 * @author Adobe Systems Inc.
 */
class NullablesSpec extends Specification {

  def 'should be able to get default string value'() {
    expect:
      Nullables.getOrDefault(null) == ''
      Nullables.getOrDefault('test1') == 'test1'
      Nullables.getOrDefault('', 'test2') == ''
      Nullables.getOrDefault(null, 'test3') == 'test3'
  }

  def 'should be able to get default value for objects'() {
    expect:
      Nullables.getOrDefault([BigInteger.valueOf(1)], [BigInteger.valueOf(2)]) == [BigInteger.valueOf(1)]
      Nullables.getOrDefault([], [1, 2, 3]) == []
      Nullables.getOrDefault(null, [1, 2, 3]) == [1, 2, 3]
  }

  def 'should be able to get default value using supplier'() {
    expect:
      Nullables.getOrDefault(null, { [1, 2, 3] } as Supplier) == [1, 2, 3]
  }

}
