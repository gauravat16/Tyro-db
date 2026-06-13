package com.gaurav.datastore.schema.datamodel;

import java.util.*;

public class Row {
    private byte[] key;
    private Map<ColumnFamily, List<Column>> columns;
    private long ttl;
    private long timeStamp;

    public Row() {
        columns = new HashMap<>();
    }

    public Row(byte[] key, Map<ColumnFamily, List<Column>> columns, long ttl, long timeStamp) {
        this.key = key;
        this.columns = columns;
        this.ttl = ttl;
        this.timeStamp = timeStamp;
    }

    public byte[] getKey() {
        return key;
    }

    public Map<ColumnFamily, List<Column>> getColumns() {
        return columns;
    }

    public long getTtl() {
        return ttl;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public void setColumns(Map<ColumnFamily, List<Column>> columns) {
        this.columns = columns;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Row row = (Row) o;
        return ttl == row.ttl && timeStamp == row.timeStamp && Objects.deepEquals(key, row.key) && Objects.equals(columns, row.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(key), columns, ttl, timeStamp);
    }

    @Override
    public String toString() {
        return "Row{" +
                "key=" + new String(key) +
                ", columns=" + columns +
                ", ttl=" + ttl +
                '}';
    }
}
