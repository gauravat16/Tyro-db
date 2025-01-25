package com.gaurav.datastore.schema;

import com.gaurav.server.config.TyroConfig;

import java.net.URI;
import java.net.URISyntaxException;

public class SchemaUtil {
    private final static String BASE_PATH = "file:///%s/tyro";
    private final static String BASE_DATA_PATH = BASE_PATH + "/data";
    private final static String NAMESPACE_DATA_PATH = BASE_DATA_PATH + "/%s";
    private final TyroConfig tyroConfig;

    public SchemaUtil(TyroConfig tyroConfig) {
        this.tyroConfig = tyroConfig;
    }

    public URI getPathForNamespace(String namespace) throws URISyntaxException {
        return new URI(NAMESPACE_DATA_PATH.formatted(tyroConfig.getDataDir(), namespace));
    }
}
