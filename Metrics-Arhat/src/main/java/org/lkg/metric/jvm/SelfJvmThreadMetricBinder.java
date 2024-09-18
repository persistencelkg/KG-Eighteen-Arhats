package org.lkg.metric.jvm;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;
import org.lkg.simple.ObjectUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Objects;

/**
 * Description: 监控完全等价于 <code>JvmThreadMetrics</code>，代码从中copy
 * 这里提供了一些定制化操作：对线程的阈值告警 & 对死锁线程的告警
 * Author: 李开广
 * Date: 2024/9/3 7:34 PM
 */
@Slf4j
public class SelfJvmThreadMetricBinder implements MeterBinder {


    @Override
    public void bindTo(MeterRegistry registry) {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        // 守护线程数
        Gauge.builder("jvm.threads.daemon", threadBean, ThreadMXBean::getDaemonThreadCount)
                .description("The current number of live daemon threads")
                .baseUnit("threads")
                .register(registry);

        // 总线程数
        Gauge.builder("jvm.threads.live", threadBean, ThreadMXBean::getThreadCount)
                .description("The current number of live threads including both daemon and non-daemon threads")
                .baseUnit("threads")
                .register(registry);

        // DEAD
        Gauge.builder("jvm.threads.states",threadBean, this::deadLockedThreadCount)
                .tags("state","DEAD")
                .baseUnit("threads")
                .register(registry);

        // NEW、RUNNABLE、BLOCK、WAITING 、TIME_WAITING、TERMINATED
        for (Thread.State state : Thread.State.values()) {
            Gauge.builder("jvm.threads.states", threadBean, (bean) -> getThreadStateCount(bean, state))
                    .tags("state", getStateTagValue(state))
                    .description("The current number of threads having " + state + " state")
                    .baseUnit("threads")
                    .register(registry);
        }
    }

    private int deadLockedThreadCount(ThreadMXBean threadMXBean) {
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        if (ObjectUtil.isEmpty(deadlockedThreads)) {
            return 0;
        }
        for (long deadlockedThread : deadlockedThreads) {
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(deadlockedThread);
            log.error("thread is dead, please attention! ", selfThreadStateException(threadInfo));
        }
        return deadlockedThreads.length;
    }

    // VisibleForTesting
    private static long getThreadStateCount(ThreadMXBean threadBean, Thread.State state) {
        ThreadInfo[] threadInfoArr = threadBean.getThreadInfo(threadBean.getAllThreadIds());
        int i = 0;
        for (ThreadInfo threadInfo : threadInfoArr) {
            if (Objects.isNull(threadInfo)) {
                continue;
            }
            Thread.State threadState = threadInfo.getThreadState();
            if (threadState == Thread.State.BLOCKED) {
                log.warn("thread exist block ", selfThreadStateException(threadInfo));
            }
            // 计数
            if (threadState == state) {
                i++;
            }
        }
        return i;
    }

    public static RuntimeException selfThreadStateException(ThreadInfo threadInfo) {
        RuntimeException runtimeException = new RuntimeException(threadInfo.toString());
        StackTraceElement[] stackTrace = threadInfo.getStackTrace();
        runtimeException.setStackTrace(stackTrace);
        return runtimeException;
    }

    private static String getStateTagValue(Thread.State state) {
        return state.name().toLowerCase().replace("_", "-");
    }

}
