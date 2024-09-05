package org.lkg.metric.redis;

import org.lkg.core.config.EnableLongHengMetric;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/4 9:05 PM
 */
@Configuration
@EnableLongHengMetric
public class RedisMetricAutoConfiguration {


    @Bean
    public BeanPostProcessor redisMetricBeanPostProcessor() {
        return new RedisTemplateMetricsBeanPostProcessor();
    }
}
