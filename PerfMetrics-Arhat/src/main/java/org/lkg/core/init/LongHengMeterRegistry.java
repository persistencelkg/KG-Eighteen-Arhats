package org.lkg.core.init;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.cumulative.CumulativeCounter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.config.LongHengStepRegistryConfig;
import org.lkg.core.config.LongHengThreadFactory;
import org.lkg.core.config.LongHongConst;
import org.lkg.core.service.MetricExporter;
import org.lkg.core.service.MetricExporterHandler;
import org.lkg.metric.threadpool.ExecutorEventTracker;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Description: 龙衡 指标观测系统
 * Author: 李开广
 * Date: 2024/8/8 2:56 PM
 */
@Slf4j
public class LongHengMeterRegistry extends StepMeterRegistry {

    private static LongHengMeterRegistry REGISTRY = getInstance();

    private final LongHengStepRegistryConfig longHengStepRegistryConfig;
    private MetricExporterHandler metricExporterHandler;

    @Getter
    private Duration interval;
    private boolean stop;

    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> scheduledFuture;

    public LongHengMeterRegistry() {
        this(new LongHengStepRegistryConfig());
        metricExporterHandler = new MetricExporterHandler();
    }

    public LongHengMeterRegistry(LongHengStepRegistryConfig longHengStepRegistryConfig) {
        this(longHengStepRegistryConfig, Clock.SYSTEM);
    }

    public LongHengMeterRegistry(LongHengStepRegistryConfig config, Clock clock) {
        super(config, clock);
        this.longHengStepRegistryConfig = config;
        // 添加默认过滤
//        addMeterFilter(MeterFilter.deny(id -> id.getName().endsWith(".percentile") && !ObjectUtils.isEmpty(id.getTag("phi"))));
        // 初始化采集间隔 && 提供动态刷新能力
        initCollectInterval();
        // add change event;
        addChangeEvent();
    }

    public synchronized static LongHengMeterRegistry getInstance() {
        if (Objects.isNull(REGISTRY)) {
            REGISTRY = new LongHengMeterRegistry();
            Metrics.addRegistry(REGISTRY);
        }
        return REGISTRY;
    }

    private void addChangeEvent() {
        // TODO listen enable key
        DynamicConfigManger.initDuration(LongHongConst.INTERVAL_KEY, this::setInterval);
        // interval key
    }

    private void initCollectInterval() {
        Duration configValueWithDefault = DynamicConfigManger.getConfigValueWithDefault(LongHongConst.INTERVAL_KEY, () -> LongHongConst.DEFAULT_INTERVAL);
        log.info(">> long-heng collect interval:{}", configValueWithDefault);
    }

    private void setInterval(Duration configValueWithDefault) {
        stop();
        if (longHengStepRegistryConfig.enabled()) {
            if (Objects.isNull(scheduledExecutorService)) {
                scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new LongHengThreadFactory());
            }
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::publish, longHengStepRegistryConfig.step()
                    .toMillis() * 1000, configValueWithDefault.toMillis() * 1000, getBaseTimeUnit());
        }
    }

    @Override
    public void stop() {
        if (Objects.isNull(scheduledFuture)) {
            return;
        }
        scheduledFuture.cancel(false);
        scheduledFuture = null;
    }

    // -------------------------------------------提供二次开发定制化能力---------------------------------------
    public void addMeterFilter(MeterFilter meterFilter) {
        super.config().meterFilter(meterFilter);
    }

    public void setPublisher(MetricExporter metricExporter){
        this.metricExporterHandler.setMetricExporter(metricExporter);
    }

    @Override
    protected void publish() {
        log.info("开始推送...");
        List<Meter> meters = getMeters();
        try {
            metricExporterHandler.exportMeter(meters);
        } catch (Exception e) {
            log.warn("export meters failed", e);
        }
    }

    @Override
    public boolean isClosed() {
        return stop;
    }

    @Override
    public TimeUnit getBaseTimeUnit() {
        return TimeUnit.MICROSECONDS;
    }


    @Override
    protected Counter newCounter(Meter.Id id) {
        return new CumulativeCounter(id);
    }
}
