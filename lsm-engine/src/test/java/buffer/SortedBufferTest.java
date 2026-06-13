package buffer;

import com.gaurav.datastore.exception.ExceptionCode;
import com.gaurav.datastore.exception.TyroException;
import com.gaurav.datastore.io.buffer.BufferEntry;
import com.gaurav.datastore.io.buffer.BufferEntryImpl;
import com.gaurav.datastore.io.buffer.SortedBuffer;
import com.gaurav.datastore.io.buffer.SortedBufferImpl;
import com.gaurav.datastore.threads.ThreadUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SortedBufferTest {
    private static final Logger logger = LogManager.getLogger(SortedBufferTest.class);


    @Test
    public void testAddingDataToBuffer_sortedOnTime() throws TyroException, ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SortedBuffer sortedBuffer = new SortedBufferImpl((e1, e2) -> Math.toIntExact(e2.getCreatedEpoch() - e1.getCreatedEpoch()),
                bufferEntries -> {
                    logger.info(bufferEntries);
                    Assert.assertEquals(1, bufferEntries.size());
                    countDownLatch.countDown();
                }, 1, 1000, 100);
        sortedBuffer.addEntry(new BufferEntryImpl("Dumbledore asked calmly".getBytes(StandardCharsets.UTF_8)));
        countDownLatch.await();
    }

    @Test
    public void testAddingDataToBuffer_sortedOnTime_multithreading() throws TyroException, ExecutionException, InterruptedException {
        Comparator<BufferEntry> comparator = (o1, o2) -> {
            int timeCompare = Long.compare(o2.getCreatedEpoch(), o1.getCreatedEpoch());
            if (timeCompare == 0) {
                return Arrays.compare(o1.getData(), o2.getData());
            } else {
                return timeCompare;
            }
        };

        CountDownLatch countDownLatch = new CountDownLatch(2);

        SortedBuffer sortedBuffer = new SortedBufferImpl(comparator,
                bufferEntries -> {
                    logger.info("Flushed size {}", bufferEntries.size());
                    Assert.assertEquals(5, bufferEntries.size());
                    countDownLatch.countDown();
                }, 10, 1000, 10);


        ExecutorService service = ThreadUtils.getVirtualThreadPool("test");

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            service.submit(() -> {
                sortedBuffer.addEntry(new BufferEntryImpl("Dumbledore asked calmly %d".formatted(finalI).getBytes(StandardCharsets.UTF_8),
                        System.currentTimeMillis() - 200));
                return 0;
            });

        }
        Thread.sleep(200);

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            service.submit(() -> {
                sortedBuffer.addEntry(new BufferEntryImpl("Dumbledore asked calmly %d".formatted(finalI).getBytes(StandardCharsets.UTF_8),
                        System.currentTimeMillis() - 200));
                return 0;
            });

        }

        countDownLatch.await();
    }

    @Test
    public void testSortingBuffer() throws TyroException, InterruptedException {
        Comparator<BufferEntry> comparator = (o1, o2) -> {
            int timeCompare = Long.compare(o1.getCreatedEpoch(), o2.getCreatedEpoch());
            if (timeCompare == 0) {
                return Arrays.compare(o1.getData(), o2.getData());
            } else {
                return timeCompare;
            }
        };

        CountDownLatch countDownLatch = new CountDownLatch(2);

        SortedBuffer sortedBuffer = new SortedBufferImpl(comparator,
                bufferEntries -> {
                    logger.info("Flushed size {}", bufferEntries.size());
                    Assert.assertEquals(5, bufferEntries.size());
                    countDownLatch.countDown();
                }, 10, 1000, 1000);

        for (int i = 0; i < 5; i++) {
            sortedBuffer.addEntry(new BufferEntryImpl("Dumbledore asked calmly %d".formatted(i).getBytes(StandardCharsets.UTF_8),
                    i));
        }
        Thread.sleep(100);

        Assert.assertEquals(0, sortedBuffer.getFirst().get().getCreatedEpoch());
        Assert.assertEquals(4, sortedBuffer.getLast().get().getCreatedEpoch());

        Optional<BufferEntry> bufferEntry = sortedBuffer.removeLast();
        Assert.assertEquals(4, bufferEntry.get().getCreatedEpoch());
        Assert.assertEquals(3, sortedBuffer.getLast().get().getCreatedEpoch());

    }

    @Test
    public void testSortingBuffer_flushing_failure() throws TyroException, InterruptedException, ExecutionException {
        Comparator<BufferEntry> comparator = (o1, o2) -> {
            int timeCompare = Long.compare(o1.getCreatedEpoch(), o2.getCreatedEpoch());
            if (timeCompare == 0) {
                return Arrays.compare(o1.getData(), o2.getData());
            } else {
                return timeCompare;
            }
        };

        SortedBuffer sortedBuffer = new SortedBufferImpl(comparator,
                bufferEntries -> {
                    logger.info("Flushed size {}", bufferEntries.size());
                    throw new TyroException("I don't flush", ExceptionCode.BAD_REQUEST);
                }, 10, 100, 100);

        sortedBuffer.addEntry(new BufferEntryImpl("Dumbledore asked calmly %d".formatted(1).getBytes(StandardCharsets.UTF_8),
                1)).get();
        Assert.assertEquals(1, sortedBuffer.getCount());
    }

    @Test
    public void testSortingBuffer_add_data_Buffer_isFull() throws TyroException, InterruptedException, ExecutionException {
        Comparator<BufferEntry> comparator = (o1, o2) -> {
            int timeCompare = Long.compare(o1.getCreatedEpoch(), o2.getCreatedEpoch());
            if (timeCompare == 0) {
                return Arrays.compare(o1.getData(), o2.getData());
            } else {
                return timeCompare;
            }
        };

        SortedBuffer sortedBuffer = new SortedBufferImpl(comparator,
                bufferEntries -> {
                    logger.info("Flushed size {}", bufferEntries.size());
                }, 10, 30, 100);

        List<Future<Boolean>> futures = new ArrayList<>();
        try {
            futures.add(sortedBuffer.addEntry(new BufferEntryImpl("Dumbledore asked calmly %d".formatted(1).getBytes(StandardCharsets.UTF_8),
                    1)));
            futures.add(sortedBuffer.addEntry(new BufferEntryImpl("Dumbledore asked calmly %d".formatted(2).getBytes(StandardCharsets.UTF_8),
                    2)));
            futures.add(sortedBuffer.addEntry(new BufferEntryImpl("Dumbledore asked calmly %d".formatted(3).getBytes(StandardCharsets.UTF_8),
                    3)));
        } catch (TyroException e) {
            Assert.assertEquals(1, sortedBuffer.getCount());
        }

        for (Future<Boolean> future : futures) {
            future.get();
        }
    }

    @Test
    public void testSortingBuffer_add_data_largerThanBuffer() throws TyroException, InterruptedException, ExecutionException {
        Comparator<BufferEntry> comparator = (o1, o2) -> {
            int timeCompare = Long.compare(o1.getCreatedEpoch(), o2.getCreatedEpoch());
            if (timeCompare == 0) {
                return Arrays.compare(o1.getData(), o2.getData());
            } else {
                return timeCompare;
            }
        };

        SortedBuffer sortedBuffer = new SortedBufferImpl(comparator,
                bufferEntries -> {
                    logger.info("Flushed size {}", bufferEntries.size());
                }, 10, 5, 100);

        try {
            sortedBuffer.addEntry(new BufferEntryImpl("Dumbledore asked calmly %d".formatted(1).getBytes(StandardCharsets.UTF_8),
                    1));
        } catch (TyroException e) {
            Assert.assertEquals(ExceptionCode.BUFFER_SIZE_EXCEEDED, e.getExceptionCode());
        }

        try {
            sortedBuffer.addEntry(null);
        } catch (TyroException e) {
            Assert.assertEquals(ExceptionCode.BAD_REQUEST, e.getExceptionCode());
        }

    }

    @Test
    public void testSortingBuffer_add_data_emptyBufferTest() throws TyroException, InterruptedException, ExecutionException {
        Comparator<BufferEntry> comparator = (o1, o2) -> {
            int timeCompare = Long.compare(o1.getCreatedEpoch(), o2.getCreatedEpoch());
            if (timeCompare == 0) {
                return Arrays.compare(o1.getData(), o2.getData());
            } else {
                return timeCompare;
            }
        };

        SortedBuffer sortedBuffer = new SortedBufferImpl(comparator,
                bufferEntries -> {
                    logger.info("Flushed size {}", bufferEntries.size());
                }, 10, 5, 100);

        Assert.assertEquals(Optional.empty(), sortedBuffer.getLast());
        Assert.assertEquals(Optional.empty(), sortedBuffer.getFirst());
        Assert.assertEquals(Optional.empty(), sortedBuffer.removeLast());
        Assert.assertEquals(0, sortedBuffer.getDataSizeSizeInBytes());
        Assert.assertEquals(0, sortedBuffer.getCount());

    }


}
