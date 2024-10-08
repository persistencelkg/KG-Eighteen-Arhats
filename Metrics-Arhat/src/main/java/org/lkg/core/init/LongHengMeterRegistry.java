package org.lkg.core.init;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.cumulative.CumulativeCounter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.config.LongHengStepRegistryConfig;
import org.lkg.core.config.LongHengThreadFactory;
import org.lkg.core.config.LongHongConst;
import org.lkg.core.meter.LongHengHistogramSupport;
import org.lkg.core.meter.histogram.LongHengDistributionSummary;
import org.lkg.core.meter.histogram.LongHengTimer;
import org.lkg.core.service.MetricExporter;
import org.lkg.core.service.MetricExporterHandler;
import org.lkg.core.service.TimerSnapshot;
import org.lkg.enums.TrueFalseEnum;
import org.lkg.metric.threadpool.ExecutorEventTracker;
import org.springframework.beans.factory.InitializingBean;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Description: 龙衡 指标观测系统
 * Author: 李开广
 * Date: 2024/8/8 2:56 PM
 */
@Slf4j
public class LongHengMeterRegistry extends StepMeterRegistry implements InitializingBean {

    public static LongHengMeterRegistry REGISTRY;

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
        addChangeEvent();
    }

    public synchronized static LongHengMeterRegistry getInstance() {
        if (Objects.isNull(REGISTRY)) {
            REGISTRY = new LongHengMeterRegistry();
            Metrics.addRegistry(REGISTRY);
            try {
                REGISTRY.afterPropertiesSet();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return REGISTRY;
    }

    private void addChangeEvent() {
        // enable key
        DynamicConfigManger.initAndRegistChangeEvent(LongHongConst.ENABLE_KEY, ref -> DynamicConfigManger.getInt(ref, 1), ref -> {
            if (TrueFalseEnum.isTrue(ref) && stop) {
                this.start(new LongHengThreadFactory());
                log.info("restart metric long heng");
                this.stop = false;
            } else {
                if (Objects.isNull(metricExporterHandler)) {
                    return;
                }
                this.publish();
                this.stop();
                for (Meter meter : this.getMeters()) {
                    meter.close();
                }
                this.stop = true;
                log.info("stop metric long heng");
            }
        });
        // interval key
        Duration duration = DynamicConfigManger.initDuration(LongHongConst.INTERVAL_KEY, LongHongConst.DEFAULT_INTERVAL, this::setInterval);
        log.info(">> long-heng collect interval:{}", duration);
    }

    private void setInterval(Duration configValueWithDefault) {
        this.interval = configValueWithDefault;
        start(new LongHengThreadFactory());
    }

    @Override
    public void start(ThreadFactory threadFactory) {
        stop();
        if (longHengStepRegistryConfig.enabled() && Objects.nonNull(interval)) {
            if (Objects.isNull(scheduledExecutorService)) {
                scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new LongHengThreadFactory());
            }
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::publish, longHengStepRegistryConfig.step()
                    .toMillis() * 1000, interval.toMillis() * 1000, getBaseTimeUnit());
        }
    }

    public static void main(String[] args) {
        System.out.println(Duration.parse("PT100s"));
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

    public void setPublisher(MetricExporter metricExporter) {
        this.metricExporterHandler.setMetricExporter(metricExporter);
    }

    @Override
    protected void publish() {
        log.info("开始拉取指标...");
        List<Meter> meters = getMeters();
        try {
            metricExporterHandler.exportMeter(meters);
        } catch (Exception e) {
            log.warn("export meters failed", e);
        } finally {
            // 清理
            meters.forEach(Metrics.globalRegistry::remove);
            for (Meter meter : meters) {
                if (meter instanceof LongHengHistogramSupport) {
                    ((LongHengHistogramSupport) meter).reset();
                }
            }
            // 清理快照
            TimerSnapshot.clear();
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

    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        return new LongHengTimer(id, distributionStatisticConfig, getBaseTimeUnit());
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        return new LongHengDistributionSummary(id, distributionStatisticConfig, scale);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.nonNull(scheduledExecutorService)) {
            ExecutorEventTracker.monit(scheduledExecutorService, "long-heng-metric-collector");
        }
    }
}
