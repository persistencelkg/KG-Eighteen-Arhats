package org.lkg.metric.threadpool;

import org.lkg.core.config.EnableLongHengMetric;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/4 3:52 PM
 */
@Configuration
@Import(ThreadPoolConfig.class)
@EnableLongHengMetric
public class ThreadPoolMetricAutoConfiguration {

//    @Bean
    public BeanPostProcessor threadPoolMetricBeanPostProcessor() {
        return new ThreadPoolMetricBeanPostProcessor();
    }

    @Bean
    public ExecutorService kgService(ThreadPoolConfig.SelfExecutorService  selfExecutorService) {
        return selfExecutorService.create("kgService", 100, null);
    }
}
