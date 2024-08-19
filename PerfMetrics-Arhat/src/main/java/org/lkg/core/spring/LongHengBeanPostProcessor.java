package org.lkg.core.spring;

import io.micrometer.core.instrument.config.MeterFilter;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricExporter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/15 11:06 AM
 */
public class LongHengBeanPostProcessor implements BeanPostProcessor {
    private static final LongHengMeterRegistry REGISTRY = LongHengMeterRegistry.getInstance();
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MetricExporter) {
            REGISTRY.setPublisher(((MetricExporter) bean));
        }
        if (bean instanceof MeterFilter) {
            REGISTRY.addMeterFilter(((MeterFilter) bean));
        }
        return bean;
    }
}
