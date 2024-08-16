package org.lkg.core.service;

import org.lkg.metric.threadpool.TrackableThreadPoolUtil;

import java.util.concurrent.ExecutorService;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/14 5:04 PM
 */
public class MetricCoreExecutor {
    // 采集 & 发布
    private static final ExecutorService publishExecutorService = TrackableThreadPoolUtil.newTrackableExecutor("metric-core");


    public static void execute(Runnable runnable) {
        publishExecutorService.submit(runnable);
    }
}
