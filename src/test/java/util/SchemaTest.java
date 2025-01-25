package util;

import com.gaurav.datastore.exception.TyroException;
import com.gaurav.datastore.schema.Namespace;
import com.gaurav.datastore.schema.SchemaUtil;
import com.gaurav.datastore.schema.Table;
import com.gaurav.datastore.schema.TyroSchema;
import com.gaurav.datastore.schema.datamodel.Column;
import com.gaurav.datastore.schema.datamodel.ColumnFamily;
import com.gaurav.datastore.schema.datamodel.Row;
import com.gaurav.datastore.schema.datamodel.proto.RowData;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaTest {

    @Test
    public void generateSchema_serializationTest() throws TyroException {

        List<ColumnFamily> columnFamilies = List.of(new ColumnFamily("cf1"), new ColumnFamily("cf2"));
        Table table = new Table("test_table", columnFamilies);
        Namespace namespace = new Namespace("test_ns", table);
        TyroSchema tyroSchema = new TyroSchema(namespace);

        Map<ColumnFamily, List<Column>> map = new HashMap<>();
        Row row = new Row("row_key".getBytes(StandardCharsets.UTF_8), map, 100, System.currentTimeMillis());

        map.put(new ColumnFamily("cf1"), List.of(new Column("col1", "My name is test".getBytes()), new Column("col2", "My name is test".getBytes())));
        map.put(new ColumnFamily("cf2"), List.of(new Column("col1", "My name is test".getBytes()), new Column("col2", "My name is test".getBytes())));

        List<String> rowSerialized = List.of("7|row_key|3|100|13|%s|8|cf1:col1|15|My name is test|8|cf1:col2|15|My name is test|".formatted(row.getTimeStamp()),
                "7|row_key|3|100|13|%s|8|cf2:col1|15|My name is test|8|cf2:col2|15|My name is test|".formatted(row.getTimeStamp()));
        List<String> serialized = SchemaUtil.getSerializedData(tyroSchema, row);

        for (int i = 0; i < rowSerialized.size(); i++) {
            Assert.assertEquals(rowSerialized.get(i), serialized.get(i));
        }
    }

    @Test
    public void generateSchema_deserializationTest() throws TyroException {

        List<ColumnFamily> columnFamilies = List.of(new ColumnFamily("cf1"), new ColumnFamily("cf2"));
        Table table = new Table("test_table", columnFamilies);
        Namespace namespace = new Namespace("test_ns", table);
        TyroSchema tyroSchema = new TyroSchema(namespace);

        Map<ColumnFamily, List<Column>> map = new HashMap<>();
        Row row = new Row("row_key".getBytes(StandardCharsets.UTF_8), map, 100, System.currentTimeMillis());

        map.put(new ColumnFamily("cf1"), List.of(new Column("col1", "My name is test".getBytes()), new Column("col2", "My name is test".getBytes())));
        map.put(new ColumnFamily("cf2"), List.of(new Column("col1", "My name is test".getBytes()), new Column("col2", "My name is test".getBytes())));

        List<String> serialized = SchemaUtil.getSerializedData(tyroSchema, row);

        Assert.assertEquals(row, SchemaUtil.getRowFromSerializedData(serialized).get(0));
    }

    @Test
    public void generateSchema_deserialization_multi_rowKey_Test() throws TyroException {

        List<ColumnFamily> columnFamilies = List.of(new ColumnFamily("cf1"), new ColumnFamily("cf2"));
        Table table = new Table("test_table", columnFamilies);
        Namespace namespace = new Namespace("test_ns", table);
        TyroSchema tyroSchema = new TyroSchema(namespace);

        Map<ColumnFamily, List<Column>> map = new HashMap<>();
        Row row = new Row("row_key".getBytes(StandardCharsets.UTF_8), map, 100, System.currentTimeMillis());

        map.put(new ColumnFamily("cf1"), List.of(new Column("col1", "My name is test".getBytes()), new Column("col2", "My name is test".getBytes())));
        map.put(new ColumnFamily("cf2"), List.of(new Column("col1", "My name is test".getBytes()), new Column("col2", "My name is test".getBytes())));

        Row row1 = new Row("row_key_1".getBytes(StandardCharsets.UTF_8), map, 100, System.currentTimeMillis());

        map.put(new ColumnFamily("cf1"), List.of(new Column("col1", "My name is test".getBytes()), new Column("col2", "My name is test".getBytes())));
        map.put(new ColumnFamily("cf2"), List.of(new Column("col1", "My name is test".getBytes()), new Column("col2", "My name is test".getBytes())));

        List<String> serialized = SchemaUtil.getSerializedData(tyroSchema, row);
        serialized.addAll(SchemaUtil.getSerializedData(tyroSchema, row1));

        Assert.assertEquals(row, SchemaUtil.getRowFromSerializedData(serialized).get(0));
        Assert.assertEquals(row1, SchemaUtil.getRowFromSerializedData(serialized).get(1));
    }

    @Test
    public void protoTest() throws InvalidProtocolBufferException {
        Map<ColumnFamily, List<Column>> map = new HashMap<>();
        map.put(new ColumnFamily("cf1"), List.of(new Column("col1", "My name is test".getBytes()),
                new Column("col2", "My name is test".getBytes())));
        map.put(new ColumnFamily("cf2"), List.of(new Column("col1", "My name is test".getBytes()),
                new Column("col2", "My name is test".getBytes())));

        Row row1 = new Row("row_key_1".getBytes(StandardCharsets.UTF_8), map, 100, System.currentTimeMillis());

        List<RowData> rowData = new ArrayList<>();
        for (ColumnFamily cf : map.keySet()){
            RowData.Builder builder = RowData.newBuilder();
            builder.setColumnFamily(com.gaurav.datastore.schema.datamodel.proto.ColumnFamily.newBuilder().setName(cf.getName()));

            List<com.gaurav.datastore.schema.datamodel.proto.Column> columns = map.get(cf)
                    .stream()
                    .map(column -> com.gaurav.datastore.schema.datamodel.proto.Column.newBuilder()
                            .setName(column.getName())
                            .setValue(ByteString.copyFrom(column.getValue())).build())
                    .toList();
            builder.addAllColumns(columns);
            rowData.add(builder.build());
        }

        com.gaurav.datastore.schema.datamodel.proto.Row row = com.gaurav.datastore.schema.datamodel.proto.Row.newBuilder()
                .setTtl(row1.getTtl())
                .setKey(ByteString.copyFrom(row1.getKey()))
                .addAllRowData(rowData)
        .build();

        byte[] serializedData = row.toByteArray();
        System.out.println("Serialized Data: " + new String(serializedData));

        Assert.assertEquals(row, row.getParserForType().parseFrom(serializedData));
    }
}
