package com.gaurav.datastore.api.impl;

import com.gaurav.datastore.api.SchemaService;
import com.gaurav.datastore.io.IOService;
import com.gaurav.datastore.schema.SchemaUtil;
import com.gaurav.datastore.schema.Table;
import com.gaurav.datastore.schema.TyroSchema;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PersistentSchemaService implements SchemaService {
    private static final Logger logger = LogManager.getLogger(PersistentSchemaService.class);

    private final IOService ioService;
    private final SchemaUtil schemaUtil;


    @Inject
    public PersistentSchemaService(IOService ioService, SchemaUtil schemaUtil) {
        this.ioService = ioService;
        this.schemaUtil = schemaUtil;
    }

    @Override
    public boolean createNamespace(String namespace) {
        try {
            return ioService.createDirectory(schemaUtil.getPathForNamespace(namespace));
        } catch (Exception exception) {
            logger.error("Failed to create namespace", exception);
        }
        return false;
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
