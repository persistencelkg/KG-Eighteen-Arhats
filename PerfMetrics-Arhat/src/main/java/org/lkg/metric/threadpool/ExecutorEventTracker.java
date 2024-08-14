package org.lkg.metric.threadpool;

import io.micrometer.core.instrument.*;
import io.micrometer.core.lang.Nullable;
import org.lkg.core.init.LongHengMeterRegistry;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.*;

/**
 * @author zhongwenjian
 * @date 2022/4/6
 */
public class ExecutorEventTracker {

    public static void monit(ExecutorService executorService, String executorName, Iterable<Tag> tags) {
        new ExecutorEventTracker(executorService, executorName, tags).bindTo(LongHengMeterRegistry.getInstance());
    }
    public static void monit(ExecutorService executorService, String executorName) {
        monit(executorService, executorName, Tags.empty());
    }

    final ExecutorService executorService;

    final Iterable<Tag> tags;

    private ExecutorEventTracker(ExecutorService executorService, String executorName,
                                 Iterable<Tag> tags) {
        this.executorService = executorService;
        this.tags = Tags.concat(tags, "name", executorName);
    }

    private void bindTo(MeterRegistry registry) {
        if (executorService == null) {
            return;
        }

        String className = executorService.getClass().getName();

        if (executorService instanceof ThreadPoolExecutor) {
            monitor(registry, (ThreadPoolExecutor) executorService);
        } else if (className.equals("java.util.concurrent.Executors$DelegatedScheduledExecutorService")) {
            monitor(registry, unwrapThreadPoolExecutor(executorService, executorService.getClass()));
        } else if (className.equals("java.util.concurrent.Executors$FinalizableDelegatedExecutorService")) {
            monitor(registry, unwrapThreadPoolExecutor(executorService, executorService.getClass().getSuperclass()));
        } else if (executorService instanceof ForkJoinPool) {
            monitor(registry, (ForkJoinPool) executorService);
        }
    }

    /**
     * Every ScheduledThreadPoolExecutor created by {@link Executors} is wrapped. Also,
     * {@link Executors#newSingleThreadExecutor()} wrap a regular {@link ThreadPoolExecutor}.
     */
    @Nullable
    private ThreadPoolExecutor unwrapThreadPoolExecutor(ExecutorService executorService, Class<?> wrapper) {
        try {
            Field e = wrapper.getDeclaredField("e");
            e.setAccessible(true);
            return (ThreadPoolExecutor) e.get(executorService);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Do nothing. We simply can't get to the underlying ThreadPoolExecutor.
        }
        return null;
    }

    private long completed(ThreadPoolExecutor tp) {
        long current = tp.getCompletedTaskCount();
        Long lastCount = countMap.put(tp, current);
        lastCount = lastCount == null ? 0L : lastCount;
        return current - lastCount;
    }

    private static final Map<ThreadPoolExecutor, Long> countMap = new WeakHashMap<>();

    private void monitor(MeterRegistry registry, @Nullable ThreadPoolExecutor tp) {
        if (tp == null) {
            return;
        }
        FunctionCounter.builder("executor.completed", tp, this::completed)
            .tags(tags)
            .baseUnit("tasks")
            .register(registry);

        Gauge.builder("executor.active", tp, ThreadPoolExecutor::getActiveCount)
            .tags(tags)
            .baseUnit("threads")
            .register(registry);

        Gauge.builder("executor.queued", tp, tpRef -> tpRef.getQueue().size())
            .tags(tags)
            .baseUnit("threads")
            .register(registry);

        Gauge.builder("executor.pool.size", tp, ThreadPoolExecutor::getPoolSize)
            .tags(tags)
            .baseUnit("threads")
            .register(registry);
    }

    private void monitor(MeterRegistry registry, ForkJoinPool fj) {
        FunctionCounter.builder("executor.steals", fj, ForkJoinPool::getStealCount)
            .tags(tags)
            .register(registry);

        Gauge.builder("executor.queued", fj, ForkJoinPool::getQueuedTaskCount)
            .tags(tags)
            .register(registry);

        Gauge.builder("executor.active", fj, ForkJoinPool::getActiveThreadCount)
            .tags(tags)
            .register(registry);

        Gauge.builder("executor.running", fj, ForkJoinPool::getRunningThreadCount)
            .tags(tags)
            .register(registry);
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        System.out.println(scheduledExecutorService instanceof  ExecutorService);
        System.out.println(scheduledExecutorService.getClass().isAssignableFrom(ThreadPoolExecutor.class));
    }
}
