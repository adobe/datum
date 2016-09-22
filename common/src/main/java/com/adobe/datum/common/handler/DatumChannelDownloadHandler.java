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

package com.adobe.datum.common.handler;

import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.common.function.DatumConsumer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adobe Systems Inc.
 * @param <T> Type of the payload.
 */
public final class DatumChannelDownloadHandler<T> extends SimpleChannelInboundHandler<T> {

  private static final Logger LOG = LoggerFactory.getLogger(DatumChannelDownloadHandler.class);

  private long readCount = 0;

  private final DatumConsumer<T> consumer;
  private final DatumCallback    callback;

  private final long startTimestamp = System.currentTimeMillis();

  public DatumChannelDownloadHandler(Class<T> payloadType, DatumConsumer<T> consumer, DatumCallback callback) {
    super(payloadType);
    this.consumer = consumer;
    this.callback = callback;
  }

  public static <T> DatumChannelDownloadHandler<T> of(Class<T> payloadType,
                                                      DatumConsumer<T> consumer,
                                                      DatumCallback callback) {
    return new DatumChannelDownloadHandler<>(payloadType, consumer, callback);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext context, T item) throws Exception {
    consumer.accept(item);
    readCount++;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
    LOG.error("unhandled exception occurred while trying to read from stream (after " + readCount + " reads). "
                  + "Stream will be closed", cause);
    context.close();
    context.executor().submit(() -> this.onError(cause));
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext context) throws Exception {
    LOG.info("{} items successfully read in {}ms", readCount, System.currentTimeMillis() - startTimestamp);
    context.close();
    context.executor().submit(this::onComplete);
  }

  private void onComplete() {
    consumer.onComplete();
    callback.onComplete();
  }

  private void onError(Throwable cause) {
    consumer.onError(cause);
    callback.onError(cause);
  }

}
