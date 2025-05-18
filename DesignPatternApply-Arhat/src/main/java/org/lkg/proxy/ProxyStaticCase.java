package org.lkg.proxy;

import lombok.AllArgsConstructor;

/**
 * @date: 2025/5/17 23:10
 * @author: li kaiguang
 */
@AllArgsConstructor
public class ProxyStaticCase implements StaticProxyCase{

    private StaticProxyCase proxyStaticCaseImpl;

    @Override
    public void request() {
        before();
        proxyStaticCaseImpl.request();
        after();
    }

    public void before() {
        System.out.println("前置处理------");
    }

    public void after() {
        System.out.println("后置处理。。。。");
    }



}
