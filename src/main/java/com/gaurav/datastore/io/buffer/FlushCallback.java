package com.gaurav.datastore.io.buffer;

import com.gaurav.datastore.exception.TyroException;

import java.util.List;

public interface FlushCallback {

    void onFlush(List<BufferEntry> bufferEntries) throws TyroException;
}
