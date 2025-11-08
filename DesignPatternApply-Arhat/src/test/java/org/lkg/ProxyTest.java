package org.lkg;

import org.junit.Test;
import org.lkg.creatable_pattern.proxy.ProxyStaticCase;
import org.lkg.creatable_pattern.proxy.StaticProxyCase;
import org.lkg.creatable_pattern.proxy.StaticProxyCaseImpl;
import org.lkg.creatable_pattern.proxy.dynamic.CglibDynamicFactory;
import org.lkg.creatable_pattern.proxy.dynamic.JdkDynamicFactory;

/**
 * @date: 2025/5/17 23:12
 * @author: li kaiguang
 */
public class ProxyTest {

    @Test
    public void testStaticProxy() {
        ProxyStaticCase proxyStaticCase = new ProxyStaticCase(new StaticProxyCaseImpl());
        proxyStaticCase.request();
    }


    @Test
    public void testDynamicProxy() {
        System.setProperty("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true"); //查看文件
        JdkDynamicFactory factory = new JdkDynamicFactory(new StaticProxyCaseImpl());
        StaticProxyCase instance = (StaticProxyCase) factory.getInstance();
        // 这句话打印对象，会触发代理对象的toString方法，而ToString方法会委托给InvokeHandler去调用
        // 所以才会，还没有执行被代理对象的方法是，就已经触发了invoke方法
        System.out.println("--》" + instance);
//        instance.request();
    }

    @Test
    public void testCglibDynamicProxy() {
        Object proxy = CglibDynamicFactory.getProxy(new StaticProxyCaseImpl());
        StaticProxyCaseImpl proxy1 = (StaticProxyCaseImpl) proxy;
        System.out.println(proxy1);
        proxy1.request();
    }
}
