package org.lkg.metric.system;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.DiskSpaceMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import org.lkg.core.config.EnableLongHengMetric;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/4 3:17 PM
 */
@EnableLongHengMetric
//@Configuration
public class SystemMetricAutoConfiguration {

    @Bean
    public MeterBinder processorMeticMeterBinder() {
        return new ProcessorMetrics();
    }

    @Bean
    public MeterBinder upTimeMeterBinder() {
        return new UptimeMetrics();
    }

    @Bean
    public MeterBinder diskMetricMeterBinder() {
        return new DiskSpaceMetrics(new File("/"));
    }

}
