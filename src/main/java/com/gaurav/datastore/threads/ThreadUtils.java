package com.gaurav.datastore.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public final class ThreadUtils {


    public static ExecutorService getVirtualThreadPool(String threadPoolName) {
        ThreadFactory factory = Thread.ofVirtual().name(threadPoolName).factory();
        return Executors.newThreadPerTaskExecutor(factory);
    }

    public static ScheduledExecutorService getScheduledThreadPool(String threadPoolName, int count) {
        ThreadFactory factory = Thread.ofVirtual().name(threadPoolName).factory();
        return Executors.newScheduledThreadPool(count, factory);
    }
}
