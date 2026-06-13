package com.gaurav.datastore.api;

import com.gaurav.datastore.exception.TyroException;
import com.gaurav.datastore.schema.Table;
import com.gaurav.datastore.schema.TyroSchema;

public interface SchemaService {
    boolean createNamespace(String namespace) throws TyroException;

    void createTable(byte[] namespace, Table table) throws TyroException;

    void updateTable(byte[] namespace, Table table) throws TyroException;

    boolean namespaceExists(byte[] namespace) throws TyroException;

    boolean tableExists(byte[] namespace, byte[] table) throws TyroException;

    TyroSchema getSchema(byte[] namespace) throws TyroException;
}
