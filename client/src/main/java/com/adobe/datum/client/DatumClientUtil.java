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

package com.adobe.datum.client;

import com.adobe.datum.common.serialize.DatumSerializer;
import com.google.protobuf.ByteString;
import java.io.IOException;
import static com.adobe.datum.common.api.DatumProto.DatumRequestProto;
import static com.adobe.datum.common.api.DatumProto.DatumRequestProto.RequestType;

/**
 * @author Adobe Systems Inc.
 */
public final class DatumClientUtil {

  private DatumClientUtil() { }

  public static <R, P> DatumRequestProto buildDatumRequest(R request,
                                                           DatumSerializer<R> requestSerializer,
                                                           Class<P> payloadClass,
                                                           RequestType requestType)
      throws IOException {
    return buildDatumRequestProto(requestType, request.getClass(), payloadClass, requestSerializer.serialize(request));
  }

  private static DatumRequestProto buildDatumRequestProto(RequestType requestType,
                                                          Class requestClass,
                                                          Class payloadType,
                                                          byte[] body) {
    return DatumRequestProto.newBuilder()
                            .setType(requestType)
                            .setParameterType(requestClass.getName())
                            .setParameterBody(ByteString.copyFrom(body))
                            .setPrototypeName(payloadType.getName())
                            .build();
  }
}
