package com.gmail.wristylotus.parquet;

import com.gmail.wristylotus.parquet.model.User;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData.Record;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.avro.AvroSchemaConverter;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;

import java.io.IOException;

import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.BINARY;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.INT32;
import static org.apache.parquet.schema.Type.Repetition.OPTIONAL;

public class ParquetApp {

    // java -jar avro-tools-1.9.1.jar compile schema avro/example.avsc ./avro
    public static void main(String[] args) throws IOException {
        String file = "./example.parquet";
        // customSchemeWriter(file);
        // customSchemeReader(file);
        autoSchemeWriter(file);
        //autoSchemeReader(file);
    }

    private static void customSchemeReader(String path) throws IOException {
        final HadoopInputFile inputFile = HadoopInputFile.fromPath(new Path(path), new Configuration());
        final ParquetReader<Record> reader = AvroParquetReader.<Record>builder(inputFile).build();
        final Record record = reader.read();
        System.out.println(record);
        reader.close();
    }

    private static void customSchemeWriter(String path) throws IOException {
        final PrimitiveType name = new PrimitiveType(OPTIONAL, BINARY, "name");
        final PrimitiveType age = new PrimitiveType(OPTIONAL, INT32, "age");
        final MessageType user = new MessageType("User", name, age);

        final Path file = new Path(path);
        final ParquetWriter<Record> writer = AvroParquetWriter.<Record>builder(file)
                .withSchema(new AvroSchemaConverter().convert(user))
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .withPageSize(65535)
                .withRowGroupSize(1024)
                .build();

        final Record record = new Record(new AvroSchemaConverter().convert(user));
        record.put("name", "Alex".getBytes());
        record.put("age", 26);

        writer.write(record);
        writer.close();
    }

    private static void autoSchemeWriter(String path) throws IOException {
        final User user = new User("Alex", 26);
        final Schema schema = user.getSchema();

        final Path file = new Path(path);

        final ParquetWriter<User> writer = AvroParquetWriter.<User>builder(file)
                .withSchema(schema)
                .withConf(prepareHdfsConfig())
                .withRowGroupSize(ParquetWriter.DEFAULT_BLOCK_SIZE)
                .withPageSize(ParquetWriter.DEFAULT_PAGE_SIZE)
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .withPageSize(65535)
                .withRowGroupSize(1024)
                .build();

        writer.write(user);
        writer.close();
    }

    private static void autoSchemeReader(String path) throws IOException {
        final HadoopInputFile inputFile = HadoopInputFile.fromPath(new Path(path), new Configuration());
        final ParquetReader<User> reader = AvroParquetReader.<User>builder(inputFile).build();
        final User user = reader.read();
        System.out.println(user);
        reader.close();
    }

    private static Configuration prepareHdfsConfig(){
        final Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://127.0.0.1:9000");
        return config;
    }

}
