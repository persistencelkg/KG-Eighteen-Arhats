package org.lkg.metric.jvm;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description: 统计每个垃圾回收期 GC
 * Author: 李开广
 * Date: 2024/9/4 2:12 PM
 */
public class SelfGcCollectionMetricBinder implements MeterBinder {

    private final Map<GarbageCollectorMXBean, Long> countMap = new HashMap<>();

    private final Map<GarbageCollectorMXBean, Long> timeMap = new HashMap<>();

    public SelfGcCollectionMetricBinder() {
        for (GarbageCollectorMXBean item : ManagementFactory.getGarbageCollectorMXBeans()) {
            countMap.put(item,0L);
            timeMap.put(item,0L);
        }
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        countMap.forEach((k, v) -> {
            Gauge.builder("jvm.gc.collectCount", k::getCollectionCount).tag("name", k.getName()).register(registry);
            Gauge.builder("jvm.gc.collectTime", k::getCollectionTime).tag("name", k.getName()).register(registry);
        });
    }
}
