package org.lkg.kafka.spring;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.checkerframework.checker.units.qual.K;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricCoreExecutor;
import org.lkg.simple.ObjectUtil;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.kafka.listener.MessageListener;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
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

        long startTime = System.nanoTime();
        boolean res = true;
        ConsumerRecord<?, ?> argument = (ConsumerRecord<?, ?>) arguments[0];
        try (TraceClose traceClose = traceHolder.newTraceScope(GETTER, argument.headers())) {
            return invocation.proceed();
        } catch (Throwable e) {
            res = false;
            throw e;
        } finally {
            final boolean finalRes = res;
            MetricCoreExecutor.execute(() -> {
                monitorProducer(finalRes, argument.topic(), startTime);
            });
        }
    }

    private void monitorProducer(boolean res, String topic, long startTime) {
        String namespace = "kafka.consume." + (res ? "suc" : "fail");
        Timer.builder(namespace)
                .tags(Tags.of("topic", topic))
                .register(LongHengMeterRegistry.getInstance())
                .record(Duration.ofNanos(System.nanoTime() - startTime));
    }
}
