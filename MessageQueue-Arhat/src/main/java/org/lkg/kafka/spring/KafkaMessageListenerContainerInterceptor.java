package org.lkg.kafka.spring;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.simple.ObjectUtil;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.kafka.listener.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/26 7:43 PM
 */
@AllArgsConstructor
public class KafkaMessageListenerContainerInterceptor implements MethodInterceptor {

    private final static String INTERCEPTOR_NAME = "onMessage";
    private final static FullLinkPropagation.Getter<Headers, String> GETTER = (headers, key) -> {
        Header header = headers.lastHeader(key);
        if (Objects.isNull(header)) {
            return null;
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    };

    private final TraceHolder traceHolder;


    public static Object proxyFactoryBean(MessageListener<?,?> messageListenerContainer, TraceHolder traceHolder) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setProxyTargetClass(true);
        proxyFactoryBean.setTarget(messageListenerContainer);
        proxyFactoryBean.addAdvice(new KafkaMessageListenerContainerInterceptor(traceHolder));
        return proxyFactoryBean.getObject();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String name = invocation.getMethod().getName();
        if (!Objects.equals(name, INTERCEPTOR_NAME)) {
            return invocation.proceed();
        }
        Object[] arguments = invocation.getArguments();
        if (ObjectUtil.isEmpty(arguments) || !(arguments[0] instanceof ConsumerRecord)) {
            return invocation.proceed();
        }
        ConsumerRecord<?, ?> argument = (ConsumerRecord<?, ?>) arguments[0];
        try (TraceClose traceClose = traceHolder.newTraceScope(GETTER, argument.headers())) {
            return invocation.proceed();
        }
    }
}
