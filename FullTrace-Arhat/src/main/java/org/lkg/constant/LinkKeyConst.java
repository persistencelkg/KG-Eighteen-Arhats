package org.lkg.constant;

import org.lkg.core.DynamicConfigManger;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/24 11:09 AM
 */
public interface LinkKeyConst {

    /**
     * 启用trace的配置key
     */
    String TRACE_BEAN_ENABLE_KEY = "full-link.enable";

    String TRACE_ID = "traceId";

    /**
     * 业务标识：userId or clientId
     */
    String USER_ID = "uid";

    /**
     * 业务标识： cityId
     */
    String CITY_ID = "cid";

    /**
     * 全链路压测标记
     */
    String STRESS_ID = "stress-flag";

    /**
     * 来自上游或者期望下游超时时间
     */
    String TC_TT = "tc-tt";



    /**
     * 支持在当前节点注入任意的key-value，且该操作是安全的并不会影响现有的trace
     * 使用场景:例如指定压测标识、临时mock header 可解决线上问题
     */
    String CUSTOM_FULL_LINK_KEY = "full-link-entry";

    /**
     * 默认全局存在的key
     */
    String DEFAULT_GLOBAL_FULL_LINK_KEY = "full.link.key";

    /**
     * to support build traceId from exist trace ecology system
     * @return assign trace key
     */
    static String getTraceIdKey() {
        return DynamicConfigManger.getConfigValue("full.trace.key", LinkKeyConst.TRACE_ID);
    }

    static String getPropagationTraceIdKey() {
        return DynamicConfigManger.getConfigValue("full.trace.propagation.key", LinkKeyConst.TRACE_ID);
    }
}
