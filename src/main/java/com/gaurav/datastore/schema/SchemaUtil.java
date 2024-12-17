package com.gaurav.datastore.schema;

import com.gaurav.datastore.exception.TyroException;
import com.gaurav.datastore.schema.datamodel.Column;
import com.gaurav.datastore.schema.datamodel.Row;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class SchemaUtil {
    private final static String DELIMINATOR = ":";
    private final static String PART_DELIMINATOR = "%s|";
    private final static String COLUMN_FAMILY_QUALIFIER = "CF" + DELIMINATOR + "%d";

    public static List<String> getSerializedData(TyroSchema tyroSchema, Row row) {
        List<String> serializedData = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();


        for (ColumnFamily columnFamily : tyroSchema.getNamespace().getTable().getColumnFamilies()) {
            String rowKey = new String(row.getKey());
            appendString(stringBuilder, rowKey);
            appendString(stringBuilder, String.valueOf(row.getTtl()));
            appendString(stringBuilder, String.valueOf(row.getTimeStamp()));

            for (Column column : row.getColumns().get(columnFamily)) {
                String columnMeta = columnFamily.getName() + DELIMINATOR + column.getName();
                appendString(stringBuilder, columnMeta);

                String value = new String(column.getValue(), StandardCharsets.UTF_8);
                appendString(stringBuilder, value);

            }

            serializedData.add(stringBuilder.toString());
            stringBuilder.setLength(0);
        }

        return serializedData;
    }

    private static void appendString(StringBuilder stringBuilder, String string) {
        stringBuilder.append(PART_DELIMINATOR.formatted(string.length()));
        stringBuilder.append(PART_DELIMINATOR.formatted(string));
    }

    public static List<Row> getRowFromSerializedData(List<String> serializedDataList) throws TyroException {
        Map<String, Row> rows = new HashMap<>();
        for (String serializedData : serializedDataList ){

            List<String> partList = getAllPartsFromString(serializedData);
            Row row = null;

            for (int i = 0; i < partList.size(); ) {
                if (i == 0) {
                    String rowKey = partList.get(i);
                    rows.computeIfAbsent(rowKey, key -> new Row());
                    row = rows.get(rowKey);
                    row.setKey(rowKey.getBytes(StandardCharsets.UTF_8));
                    i++;
                } else if (i == 1) {
                    row.setTtl(Long.parseLong(partList.get(i)));
                    i++;
                } else if (i == 2) {
                    row.setTimeStamp(Long.parseLong(partList.get(i)));
                    i++;
                } else {
                    String[] splits = partList.get(i).split(":");
                    ColumnFamily columnFamily = new ColumnFamily(splits[0]);
                    row.getColumns().putIfAbsent(columnFamily, new ArrayList<>());
                    Column column = new Column(splits[1], partList.get(++i).getBytes(StandardCharsets.UTF_8));
                    row.getColumns().get(columnFamily).add(column);
                    i++;
                }
            }
        }

        return new ArrayList<>(rows.values());
    }

    private static List<String> getAllPartsFromString(String serializedData) {
        List<String> parts = new LinkedList<>();
        StringBuilder buffer = new StringBuilder();
        int totalLen = serializedData.length();
        int i = 0;

        while (i < totalLen) {
            while (serializedData.charAt(i) != '|') {
                buffer.append(serializedData.charAt(i++));
            }
            int partLen = Integer.parseInt(buffer.toString());
            i++; //skip |
            String part = serializedData.substring(i, i + partLen);

            i += partLen + 1; //skip prefix |

            buffer.setLength(0);
            parts.add(part);
        }
        return parts;
    }
}
