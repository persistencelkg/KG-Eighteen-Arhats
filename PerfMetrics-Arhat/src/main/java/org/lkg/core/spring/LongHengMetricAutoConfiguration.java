package org.lkg.core.spring;

import com.ctrip.framework.apollo.util.ConfigUtil;
import io.micrometer.core.instrument.config.MeterFilter;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.client.KafkaProducerClient;
import org.lkg.core.config.EnableLongHengMetric;
import org.lkg.core.config.LongHongConst;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricExporter;
import org.lkg.core.service.NamespaceFilter;
import org.lkg.core.service.impl.KafkaMetricExporter;
import org.lkg.core.service.impl.SyncMetricExporter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

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
        return new SyncMetricExporter();
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
}
