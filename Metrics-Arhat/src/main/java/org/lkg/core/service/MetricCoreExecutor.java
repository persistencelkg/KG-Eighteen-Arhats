package org.lkg.core.service;

import org.lkg.metric.threadpool.TrackableThreadPoolUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/14 5:04 PM
 */
public class MetricCoreExecutor {
    // 采集 & 发布
    private static ExecutorService publishExecutorService = null;


    public static void execute(Runnable runnable) {
        if (Objects.isNull(publishExecutorService)) {
            publishExecutorService = TrackableThreadPoolUtil.newTrackableExecutor("long-heng-metric-reporter", 100000, null);
        }
        publishExecutorService.submit(runnable);
    }
}
