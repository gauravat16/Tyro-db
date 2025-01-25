package com.gaurav.datastore.schema.datamodel;

import com.gaurav.datastore.schema.Validator;

import java.util.Objects;

public class ColumnFamily implements Validator {
    private final String name;

    public ColumnFamily(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean validate() {
        return name != null && !name.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnFamily that = (ColumnFamily) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "ColumnFamily{" +
                "name='" + name + '\'' +
                '}';
    }
}
