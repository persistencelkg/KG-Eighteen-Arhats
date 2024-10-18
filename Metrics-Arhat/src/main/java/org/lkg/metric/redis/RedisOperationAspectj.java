package org.lkg.metric.redis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 *
 * Description:
 * Author: 李开广
 * Date: 2024/9/4 9:55 PM
 */
@Aspect
public class RedisOperationAspectj {

    @Around("execution(* org.springframework.data.redis.core.RedisOperations.execute*(..))")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        boolean suc = true;
        long startTime = System.nanoTime();
        String name = proceedingJoinPoint.getSignature().getName();
        String msg = Objects.equals(name, "execute") ? name + "_lua" : name;
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            suc = false;
            throw e;
        } finally {
            AbstractRedisConnectionProxy.monitorRedisCommand(suc, msg, startTime);
        }
    }

}
