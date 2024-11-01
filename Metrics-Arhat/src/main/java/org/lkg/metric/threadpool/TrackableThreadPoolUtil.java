package org.lkg.metric.threadpool;

import org.lkg.core.DynamicConfigManger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

import static org.lkg.security.KernelUtil.CPU_CORE_NUM;

/**
 * 非业务类线程，
 * 1. 不应该长期持有线程，keepAliveTime应该为0，否则业务线程池的可用线程会降低
 * 2. 拒绝策略应该选择遗弃最老的，没必要一直保留，因为和业务无关执行失败了也无所谓
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 7:25 PM
 */
public class TrackableThreadPoolUtil {

    public static ExecutorService newTrackableExecutor(String prefixName, int queueSize, RejectedExecutionHandler rejectedExecutionHandler) {
        ThreadPoolTaskExecutor executorService = new ThreadPoolTaskExecutor();
        executorService.setBeanName(prefixName);
        executorService.setThreadNamePrefix(prefixName);
        executorService.setCorePoolSize(CPU_CORE_NUM);
        executorService.setMaxPoolSize((int) (CPU_CORE_NUM * 1.5));
        executorService.setQueueCapacity(Math.min(queueSize, DynamicConfigManger.getInt("self-executor-queue-size", 100)));
        executorService.setRejectedExecutionHandler(rejectedExecutionHandler);
        executorService.setWaitForTasksToCompleteOnShutdown(true);
        executorService.setTaskDecorator(new ThreadPoolConfig.MdcTaskDecorator());
        executorService.afterPropertiesSet();
        // 可追踪，如果这里需要注释，就需要打开：ThreadPoolMetricBeanPostProcessor的注入 保证可追溯
        ExecutorEventTracker.monit(executorService.getThreadPoolExecutor(), prefixName);
        return executorService.getThreadPoolExecutor();
    }

    public static ScheduledExecutorService newTrackScheduledExecutorWithDaemon(String prefixName, int poolSize) {
        return newTrackScheduledExecutor(prefixName, poolSize, true);
    }

    public static ScheduledExecutorService newTrackScheduledExecutor(String prefixName, int pooSize, boolean daemon) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(pooSize);
        scheduledThreadPoolExecutor.setThreadFactory(r ->{
            Thread thread = new Thread(r, prefixName);
            thread.setDaemon(daemon);
            return thread;
        });
        ExecutorEventTracker.monit(scheduledThreadPoolExecutor, prefixName);
        return scheduledThreadPoolExecutor;
    }

    public static ExecutorService newTrackableExecutor(String prefixName) {
        return newTrackableExecutor(prefixName, 100, new ThreadPoolExecutor.DiscardOldestPolicy());
    }
}
