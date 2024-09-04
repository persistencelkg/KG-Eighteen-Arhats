package org.lkg.metric.jvm;

import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import org.lkg.core.config.EnableLongHengMetric;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/4 2:28 PM
 */
//@Configuration
@EnableLongHengMetric
public class JvmMetricBinderAutoConfiguration {

    // jvm 具体垃圾回收器的gc信息
    @Bean
    public MeterBinder selfGcCollectionMetricBinder() {
        return new SelfGcCollectionMetricBinder();
    }


    // jvm 线程状态信息
    @Bean
    public MeterBinder selfJvmThreadMetricBinder() {
        return new SelfJvmThreadMetricBinder();
    }

    // jvm 总体内存使用情况
    @Bean
    public MeterBinder jvmMetricBinder() {
        return new JvmMemoryMetrics();
    }


}
