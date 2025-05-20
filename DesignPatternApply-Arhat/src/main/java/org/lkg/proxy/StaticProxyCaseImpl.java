package org.lkg.proxy;

/**
 * @date: 2025/5/17 23:10
 * @author: li kaiguang
 */
public class StaticProxyCaseImpl implements StaticProxyCase{
    @Override
    public void request() {
        System.out.println("执行真正的业务逻辑");
    }
}
