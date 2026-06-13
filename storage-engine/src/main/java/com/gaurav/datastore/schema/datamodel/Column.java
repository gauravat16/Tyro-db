package com.gaurav.datastore.schema.datamodel;

import java.util.Arrays;
import java.util.Objects;

public class Column {
    private final String name;
    private final byte[] value;

    public Column(String name, byte[] value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", value=" +  new String(value) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return Objects.equals(name, column.name) && Objects.deepEquals(value, column.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(value));
    }
}
