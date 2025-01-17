package org.lkg.metric.redis;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricCoreExecutor;
import org.lkg.utils.ObjectUtil;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.data.redis.connection.RedisCommands;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 支持全局维度统计
 * Description: 基于Spring代理
 * Author: 李开广
 * Date: 2024/9/4 7:17 PM
 */
public abstract class AbstractRedisConnectionProxy<T> implements MethodInterceptor {

    private final T delegate;
    private final Set<String> redisCommandSet;
    private final List<RedisInterceptor> list;

    public AbstractRedisConnectionProxy(T delegate, List<RedisInterceptor> list) {
        this.delegate = Objects.requireNonNull(delegate, "delegate redis connection not null");
        this.list = list;
        this.redisCommandSet = Arrays.stream(RedisCommands.class.getMethods()).map(Method::getName).collect(Collectors.toSet());
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!redisCommandSet.contains(invocation.getMethod().getName())) {
            return invocation.proceed();
        }
        boolean suc = true;
        long startTime = System.nanoTime();
        try {
            return new Default(invocation.getMethod().getName(), () -> {
                try {
                    return invocation.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }).process();
        } catch (Throwable e) {
            suc = false;
            throw e;
        } finally {
            monitorRedisCommand(suc, invocation.getMethod().getName(), startTime);
        }
    }


    public static void monitorRedisCommand(boolean suc, String name, long start) {
        String namespace = "redis." + (suc ? "suc" : "fail");
        Timer.builder(namespace)
                .tags(Tags.of("cmd", name))
                .register(LongHengMeterRegistry.getInstance())
                .record(Duration.ofNanos(System.nanoTime() - start));
    }


    //   @Override for jdk  InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!redisCommandSet.contains(method.getName())) {
            return method.invoke(proxy, args);
        }
        boolean suc = true;
        long startTime = System.nanoTime();
        try {
            return method.invoke(proxy, args);
        } catch (Throwable e) {
            suc = false;
            throw e;
        } finally {
            boolean finalSuc = suc;
            // 不影响业务的执行
            MetricCoreExecutor.execute(() -> {
                monitorRedisCommand(finalSuc, method.getName(), startTime);
            });
        }
    }


    protected T getForJdk() {
//        return (T) Proxy.newProxyInstance(delegate.getClass().getClassLoader(), fetchClass(), this);
        return null;
    }

    private Class<?>[] fetchClass() {
        Class<?> aClass = delegate.getClass();
        Class<?>[] interfaces = aClass.getInterfaces();
        while (ObjectUtil.isEmpty(interfaces)) {
            aClass = aClass.getSuperclass();
            interfaces = aClass.getInterfaces();
        }
        return interfaces;
    }


    public T get() {
        ProxyFactoryBean factory = new ProxyFactoryBean();
        // 强制代理目标类
        factory.setProxyTargetClass(true);
        factory.setTarget(delegate);
        factory.addAdvice(this);
        return (T) factory.getObject();
    }

    // ------------ Default Interceptor

    private class Default implements RedisInterceptor.Chain {

        private final String cmd;
        private final Iterator<RedisInterceptor> iterator;
        private final Supplier<Object> supplier;

        public Default(String cmd, Supplier<Object> supplier) {
            this.cmd = cmd;
            this.iterator = list.iterator();
            this.supplier = supplier;
        }


        @Override
        public String cmd() {
            return cmd;
        }

        @Override
        public Object process() throws Throwable {
            return iterator.hasNext() ? iterator.next().intercept(this) : this.supplier.get();
        }
    }
}
