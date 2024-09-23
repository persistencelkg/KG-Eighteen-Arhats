package org.lkg.metric.rpc.feign;

import feign.Client;
import org.lkg.core.config.EnableLongHengMetric;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 7:11 PM
 */
@EnableLongHengMetric
@Configuration
@ConditionalOnClass(value = {FeignContext.class, Client.class})
public class FeignMetricAutoConfiguration {

    @Bean
    public BeanPostProcessor feignMetricBeanProcessor(ObjectProvider<SelfFeignInterceptor> selfFeignInterceptorObjectProvider, Environment environment, BeanFactory beanFactory) {
        return new FeignMetricBeanPostProcessor(selfFeignInterceptorObjectProvider, environment, beanFactory);
    }

    @Bean
    public SelfFeignInterceptor metricFeignRequestInterceptor() {
        return new MetricFeignRequestInterceptor();
    }
}
