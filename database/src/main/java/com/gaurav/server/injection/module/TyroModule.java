package com.gaurav.server.injection.module;

import com.gaurav.datastore.api.SchemaService;
import com.gaurav.datastore.api.impl.PersistentSchemaService;
import com.gaurav.datastore.io.IOService;
import com.gaurav.datastore.io.impl.FileIOService;
import com.gaurav.datastore.schema.SchemaUtil;
import com.gaurav.server.config.TyroConfig;
import com.gaurav.server.service.NamespaceServiceGrpc;
import com.gaurav.server.service.NamespaceServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class TyroModule extends AbstractModule {
    private final TyroConfig tyroConfig;

    public TyroModule(TyroConfig tyroConfig) {
        this.tyroConfig = tyroConfig;
    }


    @Override
    protected void configure() {
        bind(IOService.class).to(FileIOService.class).in(Singleton.class);
        bind(NamespaceServiceGrpc.NamespaceServiceImplBase.class).to(NamespaceServiceImpl.class).in(Singleton.class);
        bind(SchemaService.class).to(PersistentSchemaService.class).in(Singleton.class);
        bind(SchemaUtil.class).toInstance(new SchemaUtil(tyroConfig));
    }
}
