/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dinegri.kafka.connect.smt.test;

import com.github.dinegri.kafka.connect.smt.SchemaFilter;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.After;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;

public class SchemaFilterTest {

    private final SchemaFilter<SourceRecord> filter = new SchemaFilter<>();

    @After
    public void teardown() {
        filter.close();
    }

    @Test
    public void shouldNotFilter() {
        Map<String,String> props = new HashMap<>();
        props.put("key.schema.name", "com.github.dinegri.UserRecord");

        filter.configure(props);

        final SourceRecord record = new SourceRecord(
                null, null,
                "test", 0,
                null, null,
                null, null,
                1483425001864L
        );

        assertEquals(null, filter.apply(record));
    }

    @Test
    public void shouldFilterByKeySchemaName() {
        Map<String,String> props = new HashMap<>();
        props.put("key.schema.name", "com.github.dinegri.UserRecord");

        filter.configure(props);

        Schema userSchema = SchemaBuilder.struct()
                .name("com.github.dinegri.UserRecord")
                .field("name", Schema.STRING_SCHEMA)
                .build();

        final SourceRecord record = new SourceRecord(
                null, null,
                "test", 0,
                userSchema, null,
                null, null,
                1483425001864L
        );

		assertEquals(record, filter.apply(record));
    }

    @Test
    public void shouldFilterByValueSchemaName() {
        Map<String,String> props = new HashMap<>();
        props.put("value.schema.name", "com.github.dinegri.UserRecord");

        filter.configure(props);

        Schema userSchema = SchemaBuilder.struct()
                .name("com.github.dinegri.UserRecord")
                .field("name", Schema.STRING_SCHEMA)
                .build();

        final SourceRecord record = new SourceRecord(
                null, null,
                "test", 0,
                null, null,
                userSchema, null,
                1483425001864L
        );

        assertEquals(record, filter.apply(record));
    }

    @Test
    public void keyAndValueSchemaFilter() {
        Map<String,String> props = new HashMap<>();
        props.put("value.schema.name", "com.github.dinegri.UserRecord");

        filter.configure(props);


        Schema userKeySchema = SchemaBuilder.struct()
                .name("com.github.dinegri.UserRecord")
                .field("key", Schema.STRING_SCHEMA)
                .build();

        Schema userValueSchema = SchemaBuilder.struct()
                .name("com.github.dinegri.UserRecord")
                .field("name", Schema.STRING_SCHEMA)
                .build();

        final SourceRecord record = new SourceRecord(
                null, null,
                "test", 0,
                userKeySchema, null,
                userValueSchema, null,
                1483425001864L
        );

        assertEquals(record, filter.apply(record));
    }

}