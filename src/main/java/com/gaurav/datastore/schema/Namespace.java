package com.gaurav.datastore.schema;

public class Namespace implements Validator {
    private final String name;
    private final Table table;

    public Namespace(String name, Table table) {
        this.name = name;
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public boolean validate() {
        return table != null && table.validate();
    }
}
