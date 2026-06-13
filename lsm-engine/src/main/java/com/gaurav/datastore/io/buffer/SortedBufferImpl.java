package com.gaurav.datastore.io.buffer;

import com.gaurav.datastore.exception.ExceptionCode;
import com.gaurav.datastore.exception.TyroException;
import com.gaurav.datastore.threads.ThreadUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SortedBufferImpl implements SortedBuffer {
    private static final Logger logger = LogManager.getLogger(SortedBufferImpl.class);


    private final FlushCallback flushCallback;
    private final ConcurrentSkipListSet<BufferEntry> buffer;
    private final int maxCount;
    private final long maxSize;
    private final long timeWindowInMs;
    private final AtomicLong currentSizeInBytes = new AtomicLong(0);
    private final Lock lock = new ReentrantLock();
    private final Condition waitForFlush = lock.newCondition();
    private final ExecutorService taskExecutorService = ThreadUtils.getVirtualThreadPool("SortedBuffer-task");
    private final ScheduledExecutorService flushTimer = ThreadUtils.getScheduledThreadPool("SortedBuffer-flushTimer", 1);


    public SortedBufferImpl(Comparator<BufferEntry> comparator, FlushCallback flushCallback, int maxCount, long maxSize, long timeWindowInMs) {
        this.buffer = new ConcurrentSkipListSet<>(comparator);
        this.flushCallback = flushCallback;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
        this.timeWindowInMs = timeWindowInMs;
        flushTimer.scheduleAtFixedRate(new FlushTimerTask(this), 0, timeWindowInMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public Future<Boolean> addEntry(BufferEntry bufferEntry) throws TyroException {
        if (bufferEntry == null) throw new TyroException("Null BufferEntry passed!", ExceptionCode.BAD_REQUEST);
        if (bufferEntry.getData().length > maxSize)
            throw new TyroException("Data size more than buffer max size!", ExceptionCode.BUFFER_SIZE_EXCEEDED);

        return taskExecutorService.submit(() -> {
                    try {
                        lock.lock();
                        logger.info("Adding entry {}", bufferEntry);
                        while (currentSizeInBytes.get() + bufferEntry.getData().length > maxSize || buffer.size() == maxCount) {
                            logger.info("Buffer size {} or count {} has reached max for entry size {}, waiting for flush",
                                    currentSizeInBytes.get(), buffer.size(), bufferEntry.getData().length);
                            waitForFlush.await();
                        }
                        boolean added = buffer.add(bufferEntry);
                        currentSizeInBytes.addAndGet(bufferEntry.getData().length);
                        waitForFlush.await();
                        return added;
                    } finally {
                        lock.unlock();
                    }

                }
        );
    }

    @Override
    public Optional<BufferEntry> getLast() throws TyroException {
        try {
            lock.lock();
            if (!buffer.isEmpty()) {
                return Optional.of(buffer.last());
            }
            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<BufferEntry> getFirst() throws TyroException {
        try {
            lock.lock();
            if (!buffer.isEmpty()) {
                return Optional.of(buffer.first());
            }
            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<BufferEntry> removeLast() throws TyroException {
        try {
            lock.lock();
            Optional<BufferEntry> lastEntry = getLast();
            if (lastEntry.isPresent()) {
                if (buffer.remove(lastEntry.get())) {
                    return lastEntry;
                }
            }
            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getDataSizeSizeInBytes() {
        return currentSizeInBytes.get();
    }

    private boolean isReadyToFlush() {
        try {
            lock.lock();
            if (buffer.isEmpty()) return false;
            boolean hasTimeWindowExpired = System.currentTimeMillis() - buffer.first().getCreatedEpoch() >= timeWindowInMs;
            boolean sizeReached = buffer.size() == maxCount || currentSizeInBytes.get() == maxSize;
            return hasTimeWindowExpired || sizeReached;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getCount() {
        try {
            lock.lock();
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }

    private void clearBuffer() {
        try {
            lock.lock();
            buffer.clear();
            currentSizeInBytes.set(0);
        } finally {
            lock.unlock();
        }
    }

    private static class FlushTimerTask extends TimerTask {

        private final SortedBufferImpl sortedBuffer;

        public FlushTimerTask(SortedBufferImpl sortedBufferImpl) {
            this.sortedBuffer = sortedBufferImpl;
        }

        @Override
        public void run() {
            try {
                sortedBuffer.lock.lock();
                if (sortedBuffer.isReadyToFlush()) {
                    logger.info("FlushTimerTask isReadyToFlush!");

                    try {
                        sortedBuffer.flushCallback.onFlush(sortedBuffer.buffer.stream().toList());
                        sortedBuffer.clearBuffer();
                    } catch (Throwable e) {
                        logger.atError().log("Failed to flush! {}", e);
                    }
                }
            } finally {
                sortedBuffer.waitForFlush.signalAll();
                sortedBuffer.lock.unlock();
            }
        }
    }
}
