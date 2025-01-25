package com.gaurav.datastore.io;

import com.gaurav.datastore.exception.TyroException;

import java.net.URI;
import java.util.Optional;

public interface IOService {

    boolean createFile(URI uri, byte[] bytes) throws TyroException;

    void updateFile(URI uri, byte[] bytes) throws TyroException;

    boolean createDirectory(URI uri) throws TyroException;

    Optional<byte[]> readAllData(URI uri, byte[] bytes) throws TyroException;
}
