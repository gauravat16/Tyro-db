package com.gaurav.datastore.io.buffer;

import com.gaurav.datastore.exception.TyroException;

import java.util.Optional;
import java.util.concurrent.Future;

public interface SortedBuffer {

    Future<Boolean> addEntry(BufferEntry bufferEntry) throws TyroException;

    Optional<BufferEntry> getLast() throws TyroException;

    Optional<BufferEntry> getFirst() throws TyroException;

    Optional<BufferEntry> removeLast() throws TyroException;

    long getDataSizeSizeInBytes();

    int getCount();

}
