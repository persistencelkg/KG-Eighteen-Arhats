package org.lkg.kafka.core;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.rocketmq.client.producer.SendResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.checkerframework.checker.units.qual.K;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.kafka.spring.KafkaConsumerMethodInterceptor;
import org.lkg.kafka.spring.KafkaMessageListenerContainerInterceptor;
import org.lkg.kafka.spring.KafkaProducerMethodInterceptor;
import org.lkg.simple.ObjectUtil;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/26 4:04 PM
 */
@Aspect
@AllArgsConstructor
public class KafkaAspect {

    private final TraceHolder traceHolder;
    private static final FullLinkPropagation.Setter<Headers> setter = (headers, key, val) -> {
        headers.remove(key);
        headers.add(key, val.getBytes(StandardCharsets.UTF_8));
    };
    // 对于带有回调的拦截应该从创建client的地方入手，否则无法对回调逻辑里做trace注入操作, 这是kafka底层设计决定的，像redis多集群，因为不存在回调，所以拦截对应的操作就ok了
    @Pointcut("execution(* org.springframework.kafka.core.KafkaOperations.send*(..))")
    private void baseOnSendMethod() {
    }

    // 适合单集群方式，基于原生Template
    @Pointcut("execution(* org.springframework.kafka.core.ProducerFactory.createProducer(..))")
    private void baseOnSingleClusterCreateProducer() {
    }

    @Pointcut("execution(* org.springframework.kafka.core.ConsumerFactory.createConsumer(..))")
    private void baseOnSingleClusterCreateConsumer() {
    }

    @Pointcut("execution(* org.springframework.kafka.config.*.createListenerContainer(..))")
    private void anyCreateListenerContainer() {
    }

    @Around("baseOnSingleClusterCreateProducer()")
    public Object wrapTraceForCreateProducer(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object proceed = proceedingJoinPoint.proceed();
        if (Objects.nonNull(proceed) && proceed instanceof Producer) {
            return KafkaProducerMethodInterceptor.proxyFactoryBean(((Producer) proceed), traceHolder);
        }
        return proceed;
    }

    @Around("baseOnSingleClusterCreateConsumer()")
    public Object wrapTraceForCreateConsumer(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return KafkaConsumerMethodInterceptor.proxyFactoryBean((Consumer<?, ?>) proceedingJoinPoint.proceed(), traceHolder);

    }


    @Around("anyCreateListenerContainer()")
    public Object wrapTraceForCreateContainer(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MessageListenerContainer listener = (MessageListenerContainer) proceedingJoinPoint.proceed();
        if (Objects.isNull(listener)) {
            return proceedingJoinPoint.proceed();
        }
        ContainerProperties containerProperties = listener.getContainerProperties();
        Object proxyMessageListener = KafkaMessageListenerContainerInterceptor.proxyFactoryBean((MessageListener<?, ?>) containerProperties.getMessageListener(), traceHolder);
        listener.setupMessageListener(proxyMessageListener);
        return listener;
    }

    public static void main(String[] args) {
        Object a = null;
        MessageListenerContainer a2 = ((MessageListenerContainer) a);
        System.out.println(a2);
    }


}
