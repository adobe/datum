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

import com.adobe.datum.common.channel.metadata.ChannelMetadata;
import com.adobe.datum.common.function.DatumCallback;
import com.adobe.datum.common.function.DatumSupplier;
import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static java.lang.System.currentTimeMillis;

/**
 * @author Adobe Systems Inc.
 * @param <T> Type of the payload.
 */
public class DatumChannelUploadHandler<T> extends ChannelInboundHandlerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(DatumChannelUploadHandler.class);

  private static final int MAX_SKIP_COUNT = 5;

  private final DatumSupplier<Optional<T>> supplier;
  private final DatumCallback              callback;

  private final long startTimestamp = currentTimeMillis();

  private final AtomicInteger writeCount    = new AtomicInteger(0);
  private final AtomicBoolean writeComplete = new AtomicBoolean(false);

  public DatumChannelUploadHandler(DatumSupplier<Optional<T>> supplier, DatumCallback callback) {
    this.supplier = supplier;
    this.callback = callback;
  }

  public static <T> DatumChannelUploadHandler<T> of(DatumSupplier<Optional<T>> supplier, DatumCallback callback) {
    return new DatumChannelUploadHandler<>(supplier, callback);
  }

  @Override
  public void handlerAdded(ChannelHandlerContext context) {
    startWriting(context);
  }

  @Override
  public void channelWritabilityChanged(ChannelHandlerContext context) {
    if (!writeComplete.get() && context.channel().isWritable()) {
      startWriting(context);
    }
  }

  private void startWriting(ChannelHandlerContext context) {
    Optional item;
    while ((item = getItem(MAX_SKIP_COUNT)).isPresent() && context.channel().isWritable()) {
      context.write(item.get());
      writeCount.incrementAndGet();
    }
    if (item.isPresent()) {
      context.writeAndFlush(item.get());
      writeCount.incrementAndGet();
      writeComplete.set(false);
    } else {
      writeComplete.set(true);
      sendStreamEndMessage(context);
    }
  }

  private void sendStreamEndMessage(ChannelHandlerContext context) {
    ChannelPromise promise = context.newPromise().addListener(this::streamEnded);
    context.writeAndFlush(ChannelMetadata.streamEndMessage(), promise);
  }

  private void streamEnded(Future future) {
    Preconditions.checkState(future.isDone(), "future should be marked done when calling operationComplete");

    long timeSpent = currentTimeMillis() - startTimestamp;
    if (future.isSuccess()) {
      LOG.info("Wrote {} items to stream in {}ms.", writeCount.get(), timeSpent);
      onComplete();
    } else {
      LOG.info("Error while writing to stream. Final write count: {}, time taken: {}ms.", writeCount.get(), timeSpent);
      onError(future.cause());
    }
  }

  private Optional getItem(int skipCount) {
    try {
      return supplier.get();
    } catch (Throwable cause) {
      if (skipCount > 0) {
        LOG.warn("skipping one item due to supplier error", cause);
        return getItem(skipCount - 1);
      }
      throw new IllegalStateException("unable to continue; skips expired", cause);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
    LOG.error("unhandled exception occurred while trying to write to stream. Stream will be closed", cause);
    context.close();
    onError(cause);
  }

  private void onComplete() {
    supplier.onComplete();
    callback.onComplete();
  }

  private void onError(Throwable cause) {
    supplier.onError(cause);
    callback.onError(cause);
  }
}
