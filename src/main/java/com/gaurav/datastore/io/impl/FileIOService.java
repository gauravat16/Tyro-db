package com.gaurav.datastore.io.impl;

import com.gaurav.datastore.exception.ExceptionCode;
import com.gaurav.datastore.exception.TyroException;
import com.gaurav.datastore.io.IOService;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FileIOService implements IOService {

    @Override
    public void put(URI uri, byte[] bytes) throws TyroException {
        try {
            Path path = Paths.get(uri);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, bytes);
        } catch (Exception e) {
            throw new TyroException(e, ExceptionCode.IO_ERROR);
        }

    }

    @Override
    public Optional<byte[]> readAllData(URI uri, byte[] bytes) throws TyroException {
        try {
            Path path = Paths.get(uri);
            if (!Files.exists(path)) {
                return Optional.empty();
            }
            return Optional.of(Files.readAllBytes(path));
        } catch (Exception e) {
            throw new TyroException(e, ExceptionCode.IO_ERROR);
        }
    }
}
