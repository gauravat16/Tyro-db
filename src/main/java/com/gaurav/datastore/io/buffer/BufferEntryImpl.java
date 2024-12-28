package com.gaurav.datastore.io.buffer;

import java.util.Arrays;

public class BufferEntryImpl implements BufferEntry {
    private final byte[] data;
    private final long createdEpoch;

    public BufferEntryImpl(byte[] data) {
        this.data = data;
        this.createdEpoch = System.currentTimeMillis();
    }

    public BufferEntryImpl(byte[] data, long createdEpoch) {
        this.data = data;
        this.createdEpoch = createdEpoch;
    }

    public byte[] getData() {
        return data;
    }

    public long getCreatedEpoch() {
        return createdEpoch;
    }

    @Override
    public String toString() {
        return "BufferEntryImpl{" +
                "data=" + Arrays.toString(data) +
                ", createdEpoch=" + createdEpoch +
                '}';
    }
}
