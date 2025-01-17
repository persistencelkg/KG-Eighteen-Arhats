package org.lkg.core;

import lombok.AllArgsConstructor;
import org.lkg.constant.LinkKeyConst;
import org.lkg.utils.ObjectUtil;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Set;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/24 8:19 PM
 */
@AllArgsConstructor
public class DefaultPropagation<Carrier> implements FullLinkPropagation<Carrier>{

    private final Setter<Carrier> setter;


    @Override
    public void propagation(Trace trace, Carrier carrier) {
        Set<String> fullLinkKeySet = trace.getFullLinkKeySet();
        this.setter.set(carrier, LinkKeyConst.getPropagationTraceIdKey(), trace.getTraceId());
        if (ObjectUtil.isNotEmpty(fullLinkKeySet)) {
            // 当前上下文
            Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
            if (ObjectUtil.isNotEmpty(copyOfContextMap)) {
                fullLinkKeySet.forEach(ref -> {
                    String val = copyOfContextMap.get(ref);
                    if (ObjectUtil.isNotEmpty(val)) {
                        this.setter.set(carrier, ref, val);
                    }
                });
            }
        }
    }
}
