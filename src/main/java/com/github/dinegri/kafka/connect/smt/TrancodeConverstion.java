package com.github.dinegri.kafka.connect.smt;

import com.github.dinegri.kafka.connect.domain.Address;
import com.github.dinegri.kafka.connect.domain.Email;
import com.github.dinegri.kafka.connect.domain.Account;
import com.github.dinegri.kafka.connect.util.PojoToAvroConverter;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Map;

public class TrancodeConverstion<R extends ConnectRecord<R>> implements Transformation<R> {

    // https://karengryg.io/2018/08/25/avro-and-pojo-conversionstips-for-kafka-devs/

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
        SchemaAndValue schemaAndValue = PojoToAvroConverter.getSchemaAndValue(buildAccount());

        return record.newRecord(record.topic(),
                null,
                null,
                null,
                schemaAndValue.schema(),
                schemaAndValue.value(),
                LocalDateTime.now().getLong(ChronoField.EPOCH_DAY));
    }

    private Account buildAccount() {
        Account account = new Account();
        account.setId(10l);
        account.setName("Raul");

        Address address = new Address();
        address.setName("abc");
        account.setAddress(address);

        Email email1 = new Email();
        email1.setName("test@gmail.com");

        Email email2 = new Email();
        email2.setName("test@hotmail.com");

        account.setEmails(Arrays.asList(email1, email2));

        return account;
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void close() { }

}