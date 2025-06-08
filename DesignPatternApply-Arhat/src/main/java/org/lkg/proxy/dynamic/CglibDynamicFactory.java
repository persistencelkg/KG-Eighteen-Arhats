package org.lkg.proxy.dynamic;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @date: 2025/5/18 16:07
 * @author: li kaiguang
 */
public class CglibDynamicFactory implements MethodInterceptor {

    /**
     *
     * @param o 生成的代理对象
     * @param method 目标类对象方法
     * @param objects 目标类对象方法的参数
     * @param methodProxy 对标目标类的代理类的方法
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("cglib 前置增强");
        System.out.println(o.getClass().getName());
        System.out.println(method.getName());
        System.out.println(objects.length);
        System.out.println(methodProxy.getSuperName());

        return methodProxy.invokeSuper(o, objects);
    }

    public static Object getProxy(Object target) {
        // 生成代理类的增强器
        Enhancer enhancer = new Enhancer();
        // 设置代理类的父类字节码对象
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new CglibDynamicFactory());
        return enhancer.create();
    }
}
