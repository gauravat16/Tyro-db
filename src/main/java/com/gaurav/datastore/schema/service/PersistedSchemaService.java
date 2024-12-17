package com.gaurav.datastore.schema.service;

import com.gaurav.datastore.io.IOService;
import com.gaurav.datastore.schema.TyroSchema;

public class PersistedSchemaService implements SchemaService{
    private final IOService ioService;


    public PersistedSchemaService(IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public void createSchema(TyroSchema tyroSchema) {


    }
}
