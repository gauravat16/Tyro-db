package com.gaurav.datastore.schema;

import com.gaurav.datastore.schema.datamodel.ColumnFamily;

import java.util.List;

public class Table implements Validator {
    private final String name;
    private final List<ColumnFamily> columnFamilies;

    public Table(String name, List<ColumnFamily> columnFamilies) {
        this.name = name;
        this.columnFamilies = columnFamilies;
    }

    public String getName() {
        return name;
    }

    public List<ColumnFamily> getColumnFamilies() {
        return columnFamilies;
    }

    @Override
    public boolean validate() {
        return columnFamilies != null && columnFamilies.stream().allMatch(ColumnFamily::validate)
                && name != null && !name.isEmpty();
    }
}
