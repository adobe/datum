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

package com.adobe.datum.common.channel;

import com.adobe.datum.common.io.DataReader;
import com.adobe.datum.common.io.DataWriter;
import com.adobe.datum.common.io.DatumExternalizable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.concurrent.TimeUnit;
import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;

/**
 * @author Adobe Systems Inc.
 */
@Data
@Builder
@AllArgsConstructor
public class ConnectionSettings implements DatumExternalizable {

  private static final long serialVersionUID = 1L;

  private static final String DEFAULT_HOST                         = getProperty("datum.host", "127.0.0.1");
  private static final int    DEFAULT_PORT                         = parseInt(getProperty("datum.port", "8643"));
  private static final int    DEFAULT_CONNECT_TIMEOUT_MILLIS       = (int) TimeUnit.SECONDS.toMillis(10);
  private static final int    DEFAULT_WRITE_BUFFER_LOW_WATER_MARK  = 16 * 1024; // 16 KB
  private static final int    DEFAULT_WRITE_BUFFER_HIGH_WATER_MARK = 64 * 1024; // 64 KB
  private static final int    DEFAULT_WRITE_BANDWIDTH              = 1024 * 1024; // 1 MB/s
  private static final int    DEFAULT_READ_BANDWIDTH               = 1024 * 1024; // 1 MB/s
  private static final int    DEFAULT_BANDWIDTH_CHECK_INTERVAL     = (int) TimeUnit.SECONDS.toMillis(5);
  private static final int    DEFAULT_BANDWIDTH_WAIT_DELAY         = (int) TimeUnit.SECONDS.toMillis(5);

  private String host;
  private int    port;
  private int    connectTimeoutMillis;
  private int    writeBufferLowWaterMark;
  private int    writeBufferHighWaterMark;
  private int    writeBandwidth;
  private int    readBandwidth;
  private int    bandwidthCheckInterval;
  private int    bandwidthWaitDelay;

  public ConnectionSettings() {
    host = DEFAULT_HOST;
    port = DEFAULT_PORT;
    connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
    writeBufferHighWaterMark = DEFAULT_WRITE_BUFFER_HIGH_WATER_MARK;
    writeBufferLowWaterMark = DEFAULT_WRITE_BUFFER_LOW_WATER_MARK;
    writeBandwidth = DEFAULT_WRITE_BANDWIDTH;
    readBandwidth = DEFAULT_READ_BANDWIDTH;
    bandwidthCheckInterval = DEFAULT_BANDWIDTH_CHECK_INTERVAL;
    bandwidthWaitDelay = DEFAULT_BANDWIDTH_WAIT_DELAY;
  }

  public static ConnectionSettings getDefaultSettings() {
    return new ConnectionSettings();
  }

  @Override
  public DatumExternalizable serialize(DataWriter writer) {
    writer.write(host);
    writer.write(port);
    writer.write(connectTimeoutMillis);
    writer.write(writeBufferLowWaterMark);
    writer.write(writeBufferHighWaterMark);
    writer.write(writeBandwidth);
    writer.write(readBandwidth);
    writer.write(bandwidthCheckInterval);
    writer.write(bandwidthWaitDelay);
    return this;
  }

  @Override
  public DatumExternalizable deserialize(DataReader reader) {
    host = reader.readString();
    port = reader.readInteger();
    connectTimeoutMillis = reader.readInteger();
    writeBufferLowWaterMark = reader.readInteger();
    writeBufferHighWaterMark = reader.readInteger();
    writeBandwidth = reader.readInteger();
    readBandwidth = reader.readInteger();
    bandwidthCheckInterval = reader.readInteger();
    bandwidthWaitDelay = reader.readInteger();
    return this;
  }
}
