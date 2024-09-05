package org.lkg.metric.threadpool;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/4 4:09 PM
 */
public class ThreadPoolMetricBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        String className = bean.getClass().getName();
        if (bean instanceof ThreadPoolExecutor) {
            ExecutorEventTracker.monit((ThreadPoolExecutor) bean, beanName);
        } else if (className.equals("java.util.concurrent.Executors$DelegatedScheduledExecutorService")) {
            ExecutorEventTracker.monit((ExecutorService) bean, beanName);
        } else if (className.equals("java.util.concurrent.Executors$FinalizableDelegatedExecutorService")) {
            ExecutorEventTracker.monit((ExecutorService) bean, beanName);
        } else if (bean instanceof ForkJoinPool) {
            ExecutorEventTracker.monit(((ForkJoinPool) bean), beanName);
        }
        return bean;
    }
}
