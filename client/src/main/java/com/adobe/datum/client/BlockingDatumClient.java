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

import com.adobe.datum.common.channel.ConnectionSettings;
import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.common.function.DatumSupplier;
import com.adobe.datum.common.function.DatumUnifiedCallback;
import com.adobe.datum.common.function.UncaughtErrorReporterCallback;
import com.adobe.datum.common.serialize.SerializationContext;
import org.spockframework.util.CollectionUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static com.adobe.datum.common.function.DatumCallbackUtil.merge;

/**
 * @author Adobe Systems Inc.
 */
public final class BlockingDatumClient {

  private static final int DEFAULT_TIMEOUT_SECONDS = 5;

  private final DatumClient datumClient;

  public BlockingDatumClient() {
    this(ConnectionSettings.getDefaultSettings());
  }

  public BlockingDatumClient(SerializationContext serializationContext) {
    this(ConnectionSettings.getDefaultSettings(), serializationContext);
  }

  public BlockingDatumClient(ConnectionSettings connectionSettings) {
    this(connectionSettings, new SerializationContext());
  }

  public BlockingDatumClient(ConnectionSettings connectionSettings, SerializationContext serializationContext) {
    this(new DatumClient(connectionSettings, serializationContext));
  }

  public BlockingDatumClient(DatumClient datumClient) {
    this.datumClient = datumClient;
  }

  public <T> List<T> sendDownloadRequest(Object request, Class<T> responsePrototype) throws DatumClientException {
    return sendDownloadRequest(request, responsePrototype, DEFAULT_TIMEOUT_SECONDS);
  }

  public <T> List<T> sendDownloadRequest(Object request, Class<T> responsePrototype, int timeout)
      throws DatumClientException {
    return sendDownloadRequest(request, responsePrototype, new UncaughtErrorReporterCallback(getClass()), timeout);
  }

  public <T> List<T> sendDownloadRequest(Object request, Class<T> responsePrototype, DatumCallback callback)
      throws DatumClientException {
    return sendDownloadRequest(request, responsePrototype, callback, DEFAULT_TIMEOUT_SECONDS);
  }

  public <T> List<T> sendDownloadRequest(Object request,
                                         Class<T> responsePrototype,
                                         DatumCallback callback,
                                         int timeout)
      throws DatumClientException {
    List<T> result = new LinkedList<>();
    BlockedOperation blockedOperation = blockedOperationCallback ->
        datumClient.sendDownloadRequest(request, responsePrototype, result::add, blockedOperationCallback);
    doBlockedOperation(callback, blockedOperation, timeout);
    return result;
  }

  public <T> void sendUploadRequest(Object request, Collection<T> collection) throws DatumClientException {
    sendUploadRequest(request, collection, DEFAULT_TIMEOUT_SECONDS);
  }

  public <T> void sendUploadRequest(Object request, Collection<T> collection, int timeout)
      throws DatumClientException {
    sendUploadRequest(request, collection, new UncaughtErrorReporterCallback(getClass()), timeout);
  }

  public <T> void sendUploadRequest(Object request, Collection<T> collection, DatumCallback callback)
      throws DatumClientException {
    sendUploadRequest(request, collection, callback, DEFAULT_TIMEOUT_SECONDS);
  }

  @SuppressWarnings("unchecked")
  public <T> void sendUploadRequest(Object request, Collection<T> collection, DatumCallback callback, int timeout)
      throws DatumClientException {
    if (collection.isEmpty()) {
      callback.onComplete();
      return;
    }
    Class<T> payloadType = (Class<T>) CollectionUtil.getFirstElement(collection).getClass();
    sendUploadRequest(request, collection, payloadType, callback, timeout);
  }

  public <T> void sendUploadRequest(Object request,
                                    Collection<T> collection,
                                    Class<T> payloadType,
                                    DatumCallback callback,
                                    int timeout)
      throws DatumClientException {
    Iterator<T> iterator = collection.iterator();
    DatumSupplier<Optional<T>> supplier = () -> iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    BlockedOperation blockedOperation = blockedOperationCallback ->
        datumClient.sendUploadRequest(request, payloadType, supplier, blockedOperationCallback);
    doBlockedOperation(callback, blockedOperation, timeout);
  }

  private void doBlockedOperation(DatumCallback callback, BlockedOperation blockedOperation, int timeout)
      throws DatumClientException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    try {
      DatumCallback notifyCallback = (DatumUnifiedCallback) countDownLatch::countDown;
      blockedOperation.execute(merge(callback, notifyCallback));
      if (!countDownLatch.await(timeout, TimeUnit.SECONDS)) {
        throw new DatumClientException("operation timed out before it was completed in " + timeout + " seconds");
      }
    } catch (InterruptedException e) {
      throw new DatumClientException("blocked operation was interrupted", e);
    }
  }

  public void shutdown() {
    datumClient.shutdown();
  }

  private interface BlockedOperation {
    void execute(DatumCallback callback) throws DatumClientException;
  }
}
