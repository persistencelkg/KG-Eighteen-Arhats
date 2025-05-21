package org.lkg.proxy.dynamic;

import lombok.AllArgsConstructor;

import java.lang.reflect.Proxy;

/**
 * @date: 2025/5/17 23:14
 * @author: li kaiguang
 */
@AllArgsConstructor
public class JdkDynamicFactory {

    private Object client;
    public Object getInstance() {
        return Proxy.newProxyInstance(
                client.getClass().getClassLoader(),
                client.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    System.out.println("前置操作");
                    method.invoke(client, args);
                    System.out.println("后置操作");
                    return null;
                }

        );
    }
}
