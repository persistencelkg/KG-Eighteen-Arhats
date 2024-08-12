package org.lkg.core.config;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class LongHengThreadFactory implements ThreadFactory {
    private final AtomicInteger index = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        String prefix = "long-heng-publish-";
        Thread thread = new Thread(r);
        int seq = index.getAndIncrement();
        thread.setName(prefix + seq);
        if (!thread.isDaemon()){
            thread.setDaemon(true);
        }
        return thread;
    }
}