package org.lkg.rocketmq.core;

import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricCoreExecutor;
import org.lkg.rocketmq.core.consume.ConsumeMessageProcessJoinPointInterceptor;
import org.lkg.simple.ObjectUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/29 4:08 PM
 */
@Aspect
@AllArgsConstructor
public class RocketMqAspect {

    private final TraceHolder traceHolder;
    private final List<ConsumeMessageProcessJoinPointInterceptor> consumeMessageProcessJoinPointInterceptorList;

    private final static FullLinkPropagation.Setter<Message> SETTER = Message::putUserProperty;
    private final static FullLinkPropagation.Getter<MessageExt, String> GETTER = Message::getUserProperty;
    private final static String INTERCEPT_NAME = "onMessage";

    // 单一集群基于spring创建方式
    @Pointcut("execution(public * org.apache.rocketmq.client.producer.MQProducer.send*(..))")
    private void anyMethodForSingleCreateMqProducer() {
    }


    // 切面的本质是基于bean对象，这本来都是new出来的对象 如何能被拦截呢？
    @Pointcut("@within(org.apache.rocketmq.spring.annotation.RocketMQMessageListener)")
    private void anyConsumeMessageAnnotation() {
    }


    @Around("anyMethodForSingleCreateMqProducer()")
    public Object aroundForProducer(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        if (ObjectUtil.isEmpty(args)) {
            return proceedingJoinPoint.proceed();
        }
        Object first = args[0];
        Message message = (Message) first;
        try (TraceClose traceClose = traceHolder.newTraceScope(SETTER, message)) {
            for (int i = 0; i < args.length; i++) {
                // SendCallback是否是args[1] 接口、父接口类型
                if (SendCallback.class.isAssignableFrom(args[i].getClass())) {
                    args[i] = wrapMessage(((SendCallback) args[i]), traceHolder, traceClose);
                    break;
                }
            }
        }
        long startTime = System.nanoTime();
        boolean res = true;
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            res = false;
            throw e;
        } finally {
            monitorProducer(message.getTopic(), res, startTime);
        }

    }

    static void monitorProducer(String topic, boolean res, long startTime) {
        String namespace = "rocket.send." + (res ? "suc" : "fail");
        MetricCoreExecutor.execute(() -> {
            Timer.builder(namespace)
                    .tags("topic", topic)
                    .register(LongHengMeterRegistry.getInstance())
                    .record(Duration.ofNanos(System.nanoTime() - startTime));
        });
    }


    @Around("anyConsumeMessageAnnotation()")
    public Object aroundForConsumeMessage(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String name = proceedingJoinPoint.getSignature().getName();
        if (!Objects.equals(name, INTERCEPT_NAME) || ObjectUtil.isEmpty(proceedingJoinPoint.getArgs()) || proceedingJoinPoint.getArgs().length != 1) {
            return proceedingJoinPoint.proceed();
        }
        return new DefaultChain(proceedingJoinPoint, proceedingJoinPoint.getArgs()).process();
    }


    static Object wrapMessage(SendCallback object, TraceHolder traceHolder, TraceClose traceClose) {
        return new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                try (TraceClose traceClose1 = traceHolder.newTraceScope(traceClose.getTrace())) {
                    object.onSuccess(sendResult);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onException(Throwable e) {
                try (TraceClose traceClose1 = traceHolder.newTraceScope(traceClose.getTrace())) {
                    object.onException(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }


    class DefaultChain implements ConsumeMessageProcessJoinPointInterceptor.SelfChain {

        private final Iterator<ConsumeMessageProcessJoinPointInterceptor> iterator;
        private final ProceedingJoinPoint joinPoint;
        private Object[] args;

        public DefaultChain(ProceedingJoinPoint joinPoint, Object[] args) {
            this.joinPoint = joinPoint;
            this.args = args;
            this.iterator = consumeMessageProcessJoinPointInterceptorList.iterator();
        }


        @Override
        public Object process() throws Throwable {
            return iterator.hasNext() ? iterator.next().intercept(this) : joinPoint.proceed(args);
        }

        @Override
        public Object process(Object[] args) throws Throwable {
            this.args = args;
            return process();
        }

        @Override
        public Object[] args() {
            return args;
        }

        @Override
        public MethodSignature methodSignature() {
            return (MethodSignature) joinPoint.getSignature();
        }
    }
}
