package org.lkg.config;

import org.lkg.core.DynamicConfigManger;
import org.lkg.core.DynamicConfigService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/20 2:31 PM
 */
public class DynamicConfigBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DynamicConfigService) {
            DynamicConfigManger.registerConfigService(((DynamicConfigService) bean));
        }
        return bean;
    }
}
