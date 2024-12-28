package com.gaurav.datastore.io.buffer;

public interface BufferEntry {

    byte[] getData();

    long getCreatedEpoch();
}
