package com.gaurav.datastore.api.impl;

import com.gaurav.datastore.api.DataService;
import com.gaurav.datastore.schema.datamodel.Row;

public class LSMDataService implements DataService {

    @Override
    public void put(String namespace, String table, Row row) {

    }

    @Override
    public Row get(String namespace, String table, String key) {
        return null;
    }
}
