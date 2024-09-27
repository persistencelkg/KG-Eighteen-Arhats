package org.lkg.core;

/**
 * Description: 全链路通用配置传播器：such as; trace,user_id,city_id,压测标识
 * Author: 李开广
 * Date: 2024/9/24 7:49 PM
 */
public interface FullLinkPropagation<Carrier> {

    void propagation(Trace trace, Carrier carrier);

    interface Setter<Carrier> {
        void set(Carrier c, String key, String value);
    }

    interface Getter<Carrier, T> {
        T get(Carrier c, String key);
    }
}
