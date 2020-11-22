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
package com.github.dinegri.kafka.connect.smt;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.util.Map;

public class SchemaFilter<R extends ConnectRecord<R>> implements Transformation<R> {

    public static final String OVERVIEW_DOC =
            "Filter a record by schema.";

    public static final ConfigDef CONFIG_DEF;

    static {
        CONFIG_DEF = new ConfigDef()
                .define(ConfigName.VALUE_SCHEMA, ConfigDef.Type.STRING, "", ConfigDef.Importance.HIGH,OVERVIEW_DOC)
                .define(ConfigName.KEY_SCHEMA, ConfigDef.Type.STRING, "", ConfigDef.Importance.HIGH,OVERVIEW_DOC);
    }

    private interface ConfigName {
        String KEY_SCHEMA = "key.schema.name";
        String VALUE_SCHEMA = "value.schema.name";
    }

    private String keySchema;
    private String valueSchema;

    @Override
    public void configure(Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);

        keySchema = config.getString(ConfigName.KEY_SCHEMA);
        valueSchema = config.getString(ConfigName.VALUE_SCHEMA);
    }

    @Override
    public R apply(R record) {
        boolean hasKeyConfigured = !keySchema.isEmpty();
        boolean hasValueConfigured = !valueSchema.isEmpty();

        String keySchemaName = record.keySchema() != null ? record.keySchema().name() : "";
        String valueSchemaName = record.valueSchema() != null ? record.valueSchema().name() : "";

        R result = null;

        if (hasKeyConfigured && hasValueConfigured) {
            result = keySchemaName.equals(keySchema) && valueSchemaName.equals(valueSchema) ? record : null;
        } else if (hasKeyConfigured) {
            result = keySchemaName.equals(keySchema) ? record : null;
        } else if (hasValueConfigured) {
            result = valueSchemaName.equals(valueSchema) ? record : null;
        }

        return result;
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void close() { }
}