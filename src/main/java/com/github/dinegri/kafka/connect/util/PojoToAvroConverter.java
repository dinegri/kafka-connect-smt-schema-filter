package com.github.dinegri.kafka.connect.util;

import io.confluent.connect.avro.AvroData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.connect.data.SchemaAndValue;

import java.io.ByteArrayOutputStream;

public class PojoToAvroConverter {

    private static <T> GenericRecord convert(T model)  {
        try {
            org.apache.avro.Schema schema = ReflectData.get().getSchema(model.getClass());

            ReflectDatumWriter<T> datumWriter = new ReflectDatumWriter<>(schema);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            datumWriter.write(model, encoder);
            encoder.flush();

            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(outputStream.toByteArray(), null);

            return datumReader.read(null, decoder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> SchemaAndValue getSchemaAndValue(T model) {
        final GenericRecord genericRecord = convert(model);
        final int avroSchemaCacheSize = 1000;
        io.confluent.connect.avro.AvroData avroData = new AvroData(avroSchemaCacheSize);
        return avroData.toConnectData(genericRecord.getSchema(), genericRecord);
    }

}
