/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.flume;

import com.google.common.collect.ImmutableMap;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.kafka.common.utils.SystemTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.header.Headers;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Map;

class EventConverter {
  final FlumeAvroSourceConnectorConfig config;

  final Map<String, Object> sourcePartition = ImmutableMap.of();
  final Map<String, Object> sourceOffset = ImmutableMap.of();
  Time time = new SystemTime();


  public EventConverter(FlumeAvroSourceConnectorConfig config) {
    this.config = config;
  }

  SourceRecord record(AvroFlumeEvent event, String sender) {

    Headers headers = new ConnectHeaders();
    if (null != event.getHeaders()) {
      event.getHeaders().forEach((key, value) -> {
        if (null != value) {
          String headerName = String.format("flume.%s", key);
          String v = value.toString();
          headers.addString(headerName, v);
        }
      });
    }
    return new SourceRecord(
        sourcePartition,
        sourceOffset,
        this.config.topic,
        null,
        null,
        null,
        Schema.BYTES_SCHEMA,
        event.getBody(),
        this.time.milliseconds(),
        headers
    );
  }
}
