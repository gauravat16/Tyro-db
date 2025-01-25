package com.gaurav.datastore.io.impl;

import com.gaurav.datastore.exception.ExceptionCode;
import com.gaurav.datastore.exception.TyroException;
import com.gaurav.datastore.io.IOService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.nio.file.*;
import java.util.Optional;

public class FileIOService implements IOService {
    private static final Logger logger = LogManager.getLogger(FileIOService.class);


    @Override
    public void updateFile(URI uri, byte[] bytes) throws TyroException {
        try {
            Path path = Paths.get(uri);
            if (!Files.exists(path)) {
                throw new TyroException("File doesn't exist!", ExceptionCode.IO_ERROR);
            }
            Files.write(path, bytes, StandardOpenOption.APPEND);
        } catch (Exception e) {
            throw new TyroException(e, ExceptionCode.IO_ERROR);
        }
    }

    @Override
    public boolean createDirectory(URI uri) throws TyroException {
        try {
            Path path = Paths.get(uri);
            if (Files.isDirectory(path)){
                return false;
            }
            Files.createDirectories(path);
            return true;
        } catch (Exception e) {
            throw new TyroException(e, ExceptionCode.IO_ERROR);
        }
    }

    @Override
    public boolean createFile(URI uri, byte[] bytes) throws TyroException {
        try {
            Path path = Paths.get(uri);
            if (!Files.exists(path)) {
                Files.createFile(path);
                Files.write(path, bytes);
                return true;
            }
            return false;
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
