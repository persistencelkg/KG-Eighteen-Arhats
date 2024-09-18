package org.lkg.metric.api;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/5 7:15 PM
 */
public class MetricFilterBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CommonFilter) {
            // 用ObjectProvider 迭代器来实现
        }
        return bean;
    }
}
