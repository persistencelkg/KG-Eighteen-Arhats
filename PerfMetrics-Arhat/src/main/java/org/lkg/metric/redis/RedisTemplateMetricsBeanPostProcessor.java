package org.lkg.metric.redis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Description: AOP是在spring 启动的时候进行加载的，因此在做切面时 需要保证被切面对象已经被spring 实例化了
 * 如果都没有进行实例化 ，比如在@Bean编写阶段是通过属性填充实例化的，就无法被管，Ag： 在redis注入中，实际注入的redisTemplate，而RedisConnectionFactory是以属性的方式注入，并不交给spring 管理，
 * 所以只能通过redisTemplate初始化时来实现
 * Author: 李开广
 * Date: 2024/9/4 7:08 PM
 */
public class RedisTemplateMetricsBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RedisTemplate) {
            RedisTemplate<?, ?> template = (RedisTemplate<?, ?>) bean;
            RedisConnectionFactory proceed = template.getRequiredConnectionFactory();
            template.setConnectionFactory(new RedisMetricConnectionFactory(proceed));
        }
        return bean;
    }

}
