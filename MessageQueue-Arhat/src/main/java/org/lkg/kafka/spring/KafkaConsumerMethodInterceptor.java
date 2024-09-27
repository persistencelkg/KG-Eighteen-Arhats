package org.lkg.kafka.spring;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.header.Headers;
import org.checkerframework.checker.units.qual.K;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.springframework.aop.framework.ProxyFactoryBean;

import java.nio.charset.StandardCharsets;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/26 4:33 PM
 */
@AllArgsConstructor
public class KafkaConsumerMethodInterceptor<K, V> implements MethodInterceptor {

    private final TraceHolder traceHolder;

    private static final FullLinkPropagation.Getter<Headers, String> getter = (headers, key) -> new String(headers.lastHeader(key).value(), StandardCharsets.UTF_8);


    public static <K, V> Object proxyFactoryBean(Consumer<K, V> proceed, TraceHolder traceHolder) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setProxyTargetClass(true);
        proxyFactoryBean.setTarget(proceed);
        proxyFactoryBean.addAdvice(new KafkaConsumerMethodInterceptor<>(traceHolder));
        return proxyFactoryBean.getObject();

    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object proceed = invocation.proceed();
        if (!(proceed instanceof ConsumerRecord)) {
            return proceed;
        }
        ConsumerRecord<K, V> record = (ConsumerRecord<K, V>) proceed;
        try (TraceClose traceClose = traceHolder.newTraceScope(getter, record.headers())) {
            return record;
        }
    }
}
