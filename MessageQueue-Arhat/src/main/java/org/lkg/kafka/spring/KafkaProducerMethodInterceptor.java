package org.lkg.kafka.spring;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.checkerframework.checker.units.qual.K;
import org.lkg.core.*;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/26 4:28 PM
 */
@AllArgsConstructor
public class KafkaProducerMethodInterceptor<K, V> implements MethodInterceptor {

    private final TraceHolder traceHolder;
    private static final FullLinkPropagation.Setter<Headers> setter = (headers, key, val) -> {
      headers.remove(key);
      headers.add(key, val.getBytes(StandardCharsets.UTF_8));
    };

    private final static String method= "addCallback";

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!Objects.equals(invocation.getMethod().getName(), method)) {
            return invocation.proceed();
        }
        Object[] args = invocation.getArguments();
        if (args == null || args.length == 0) {
            return invocation.proceed();
        }
        ListenableFutureCallback arg = (ListenableFutureCallback) args[0];

        // 透传
        try (TraceClose traceClose = traceHolder.newTraceScope(TraceContext.getCurrentContext())) {
            return invocation.proceed();
        }
    }


    public static <K, V> Object proxyFactoryBean(SettableListenableFuture template, TraceHolder traceHolder) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        // 是否直接代理，默认代理接口
        proxyFactoryBean.setProxyTargetClass(true);
        proxyFactoryBean.setTarget(template);
        proxyFactoryBean.addAdvice(new KafkaProducerMethodInterceptor<>(traceHolder));
        return proxyFactoryBean.getObject();
    }

}
