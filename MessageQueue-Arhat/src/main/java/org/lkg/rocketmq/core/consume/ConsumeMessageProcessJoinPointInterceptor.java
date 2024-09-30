package org.lkg.rocketmq.core.consume;

import org.apache.rocketmq.common.message.MessageExt;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * 基于pjp的通用拦截器，基于代理的拦截，这么做是为了适配一些无法直接通过
 * 实现目标拦截器的处理
 * Description:
 * Author: 李开广
 * Date: 2024/9/30 10:23 AM
 */
public interface ConsumeMessageProcessJoinPointInterceptor extends Ordered {

    Object intercept(SelfChain selfChain);

    interface SelfChain {

        Object process() throws Throwable;

        Object process(Object[] args) throws Throwable;

        Object[] args();

        MethodSignature methodSignature();
    }
}
