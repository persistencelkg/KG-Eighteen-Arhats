package org.lkg.kafka.spring;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.Trace;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricCoreExecutor;
import org.springframework.aop.framework.ProxyFactoryBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.time.Duration;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/26 4:28 PM
 */
@AllArgsConstructor
public class KafkaProducerMethodInterceptor<K, V> implements MethodInterceptor {

    private final TraceHolder traceHolder;
    private final static String PRODUCER_METRIC = "kafka.send.";

    private static final FullLinkPropagation.Setter<Headers> setter = (headers, key, val) -> {
        headers.remove(key);
        headers.add(key, val.getBytes(StandardCharsets.UTF_8));
    };


    //    private final static String method= "addCallback";
    private final static String method = "send";

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!Objects.equals(invocation.getMethod().getName(), method)) {
            return invocation.proceed();
        }
        Object[] args = invocation.getArguments();
        if (args == null || args.length == 0) {
            return invocation.proceed();
        }
        ProducerRecord<?, ?> arg = (ProducerRecord) args[0];
        boolean res = true;
        long startTime = System.nanoTime();
        // 透传
        try (TraceClose traceClose = traceHolder.newTraceScope(setter, arg.headers())) {
            if (args[1] instanceof Callback) {
                args[1] = wrapCallBackWithTrace(((Callback) args[1]), traceHolder, traceClose);
            }
            return invocation.proceed();
        } catch (Throwable e) {
            res = false;
            throw e;
        } finally {
            //  发送成功数
            // 不影响业务的执行
            boolean finalRes = res;
            MetricCoreExecutor.execute(() -> {
                monitorProducer(finalRes, arg.topic(), startTime);
            });
        }
    }

    private void monitorProducer(boolean res, String topic, long startTime) {
        String namespace = PRODUCER_METRIC + (res ? "suc" : "fail");
        Timer.builder(namespace)
                .tags(Tags.of("topic", topic))
                .register(LongHengMeterRegistry.getInstance())
                .record(Duration.ofNanos(System.nanoTime() - startTime));
    }

    private Callback wrapCallBackWithTrace(Callback arg, TraceHolder traceHolder, TraceClose trace) {
        return (metadata, exception) -> {
            try (TraceClose traceClose = traceHolder.newTraceScope(trace.getTrace())) {
                arg.onCompletion(metadata, exception);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <K, V> Object proxyFactoryBean(Producer<K, V> template, TraceHolder traceHolder) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        // 是否直接代理，默认代理接口
        proxyFactoryBean.setProxyTargetClass(true);
        proxyFactoryBean.setTarget(template);
        proxyFactoryBean.addAdvice(new KafkaProducerMethodInterceptor<>(traceHolder));
        return proxyFactoryBean.getObject();
    }

}
