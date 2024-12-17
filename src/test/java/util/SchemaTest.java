package util;

import com.gaurav.datastore.exception.TyroException;
import com.gaurav.datastore.schema.*;
import com.gaurav.datastore.schema.datamodel.Column;
import com.gaurav.datastore.schema.datamodel.Row;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

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
}
