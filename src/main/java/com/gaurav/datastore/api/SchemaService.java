package com.gaurav.datastore.api;

import com.gaurav.datastore.schema.Table;
import com.gaurav.datastore.schema.TyroSchema;

public interface SchemaService {
    void createNamespace(byte[] namespace);

    void createTable(byte[] namespace, Table table);

    void updateTable(byte[] namespace, Table table);

    boolean namespaceExists(byte[] namespace);

    boolean tableExists(byte[] namespace, byte[] table);

    TyroSchema getSchema(byte[] namespace);
}
