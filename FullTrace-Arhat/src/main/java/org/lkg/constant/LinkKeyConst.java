package org.lkg.constant;

import org.lkg.core.DynamicConfigManger;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/24 11:09 AM
 */
public interface LinkKeyConst {

    String TRACE_ID = "traceId";

    String USER_ID = "uid";

    String CITY_ID = "cid";

    String STRESS_ID = "stress-flag";

    /**
     * 来自上游或者期望下游超时时间
     */
    String TC_TT = "tc-tt";


    String TRACE_BEAN_ENABLE_KEY = "full-link.enable";

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
