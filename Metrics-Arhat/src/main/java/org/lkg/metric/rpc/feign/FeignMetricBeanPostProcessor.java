package org.lkg.metric.rpc.feign;

import feign.Contract;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 4:08 PM
 */
public class FeignMetricBeanPostProcessor implements BeanPostProcessor {

    private final List<SelfFeignInterceptor> list = new ArrayList<>();
    private final Environment environment;
    private final BeanFactory beanFactory;

    public FeignMetricBeanPostProcessor(ObjectProvider<SelfFeignInterceptor> selfFeignInterceptorObjectProvider, Environment environment, BeanFactory beanFactory) {
        selfFeignInterceptorObjectProvider.forEach(list::add);
        this.environment = environment;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof FeignContext) {
            return new FeignContextDecorator(list, environment, beanFactory, ((FeignContext) bean));
        }
        if (bean instanceof Contract) {
            return FeignMetaDataMethodInterceptor.getProxy(environment, ((Contract) bean));
        }
        return bean;
    }
}
