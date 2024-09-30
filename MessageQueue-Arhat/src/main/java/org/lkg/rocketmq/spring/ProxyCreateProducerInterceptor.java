package org.lkg.rocketmq.spring;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.common.message.Message;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.rocketmq.core.RocketMqAspect;
import org.springframework.aop.framework.ProxyFactoryBean;

import java.util.Collection;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/30 7:23 PM
 */
@AllArgsConstructor
public class ProxyCreateProducerInterceptor implements MethodInterceptor {

    private final TraceHolder traceHolder;
    private final static String INTERCEPTOR_NAME_LIKE = "send";
    private final static FullLinkPropagation.Setter<Message> SETTER = Message::putUserProperty;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String name = invocation.getMethod().getName();
        if (!name.contains(INTERCEPTOR_NAME_LIKE)) {
            return invocation.proceed();
        }
        Object[] arguments = invocation.getArguments();
        Object arg = arguments[0];
        if (arg instanceof Message) {
            try (TraceClose traceClose = traceHolder.newTraceScope(SETTER, ((Message) arg))) {
                for (int i = 0; i < arguments.length; i++) {
                    if (SendCallback.class.isAssignableFrom(arguments[i].getClass())) {
                        arguments[i] = RocketMqAspect.wrapMessage(((SendCallback) arguments[i]), traceHolder, traceClose);
                        break;
                    }
                }
            }
            ;
        }
        return invocation.proceed();
    }

    public static Object createProducer(MQProducer mqProducer, TraceHolder traceHolder) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(mqProducer);
        proxyFactoryBean.setProxyTargetClass(true);
        proxyFactoryBean.addAdvice(new ProxyCreateProducerInterceptor(traceHolder));
        return proxyFactoryBean.getObject();
    }
}
