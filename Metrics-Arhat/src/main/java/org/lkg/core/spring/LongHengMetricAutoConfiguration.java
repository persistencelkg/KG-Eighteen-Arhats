package org.lkg.core.spring;

import io.micrometer.core.instrument.config.MeterFilter;
import org.lkg.core.client.KafkaProducerClient;
import org.lkg.core.config.EnableLongHengMetric;
import org.lkg.core.config.LongHongConst;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricExporter;
import org.lkg.core.service.NamespaceFilter;
import org.lkg.core.service.impl.KafkaMetricExporter;
import org.lkg.core.service.impl.SyncMetricExporter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/15 11:12 AM
 */

@Configuration
@EnableLongHengMetric // 默认启用
public class LongHengMetricAutoConfiguration {

    @Bean
    public BeanPostProcessor longHengBeanPostProcessor() {
        return new LongHengBeanPostProcessor();
    }


    @Bean
    public KafkaProducerClient kafkaProducerClient() {
        return KafkaProducerClient.getInstance();
    }


    // 数据上报
    @Bean
    @ConditionalOnMissingBean(MetricExporter.class)
    public MetricExporter metricExporter() {
        return new KafkaMetricExporter();
    }


    // 大小过滤
    @Bean
    public MeterFilter limitMeterSizeFilter() {
        return MeterFilter.deny(ref -> LongHengMeterRegistry.getInstance().getMeters().size() > LongHongConst.MAX_NAMESPACE_COUNT);
    }

    // 命名空间过滤
    @Bean
    public MeterFilter namespaceMeterFilter() {
        return MeterFilter.deny(ref -> NamespaceFilter.disable(ref.getName()));
    }

    // Timer 自动添加百分比的监控
//    @Bean
    public MeterFilter percentDenyFilter() {
        return MeterFilter.deny(id -> id.getName().endsWith(".percentile") && id.getTag("phi") != null);
    }
}
