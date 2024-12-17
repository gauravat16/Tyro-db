package com.gaurav.datastore.api.impl;

import com.gaurav.datastore.api.SchemaService;
import com.gaurav.datastore.schema.Table;
import com.gaurav.datastore.schema.TyroSchema;

public class PersistentSchemaService implements SchemaService {


    @Override
    public void createNamespace(byte[] namespace) {

    }

    @Override
    public void createTable(byte[] namespace, Table table) {

    }

    @Override
    public void updateTable(byte[] namespace, Table table) {

    }

    @Override
    public boolean namespaceExists(byte[] namespace) {
        return false;
    }

    @Override
    public boolean tableExists(byte[] namespace, byte[] table) {
        return false;
    }

    @Override
    public TyroSchema getSchema(byte[] namespace) {
        return null;
    }
}
