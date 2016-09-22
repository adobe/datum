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

package com.adobe.datum.example.runner;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Adobe Systems Inc.
 */
public final class ExampleRunner {

  private static final Logger LOG = LoggerFactory.getLogger(ExampleRunner.class);

  private static final Map<String, Class> EXAMPLE_DRIVERS =
      ImmutableMap.<String, Class>builder()
                  .put("protobuf-server", com.adobe.datum.example.protobuf.server.ExampleServerDriver.class)
                  .put("protobuf-client", com.adobe.datum.example.protobuf.client.ExampleClientDriver.class)
                  .put("serializable-server", com.adobe.datum.example.serializable.server.ExampleServerDriver.class)
                  .put("serializable-client", com.adobe.datum.example.serializable.client.ExampleClientDriver.class)
                  .put("json-server", com.adobe.datum.example.json.server.ExampleServerDriver.class)
                  .put("json-client", com.adobe.datum.example.json.client.ExampleClientDriver.class)
                  .put("kryo-server", com.adobe.datum.example.kryo.server.ExampleServerDriver.class)
                  .put("kryo-client", com.adobe.datum.example.kryo.client.ExampleClientDriver.class)
                  .build();

  @SuppressWarnings("unchecked")
  public static void main(String[] args)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String applicationType = args[0];
    Preconditions.checkArgument(EXAMPLE_DRIVERS.containsKey(applicationType),
                                "unknown application type: " + applicationType + ". "
                                    + "Please choose one of " + EXAMPLE_DRIVERS.keySet());
    LOG.info("starting application type '{}' with driver: {}", applicationType, EXAMPLE_DRIVERS.get(applicationType));
    Class applicationDriverClass = EXAMPLE_DRIVERS.get(applicationType);
    applicationDriverClass.getMethod("main", String[].class).invoke(null, (Object) new String[] {});
  }
}
