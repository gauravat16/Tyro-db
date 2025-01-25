package util;

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
