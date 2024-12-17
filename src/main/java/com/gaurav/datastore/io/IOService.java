package com.gaurav.datastore.io;

import com.gaurav.datastore.exception.TyroException;

import java.net.URI;
import java.util.Optional;

public interface IOService {

    void put(URI uri, byte[] bytes) throws TyroException;

    Optional<byte[]> readAllData(URI uri, byte[] bytes) throws TyroException;
}
