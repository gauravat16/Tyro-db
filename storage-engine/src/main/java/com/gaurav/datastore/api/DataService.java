package com.gaurav.datastore.api;

import com.gaurav.datastore.schema.datamodel.Row;

public interface DataService {

    void put(String namespace, String table, Row row);

    Row get(String namespace, String table, String key);

}
