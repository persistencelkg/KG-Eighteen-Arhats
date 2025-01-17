package org.lkg.core;

import org.lkg.constant.LinkKeyConst;
import org.lkg.utils.ObjectUtil;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/24 2:51 PM
 */
public class DefaultTraceDecorator implements TraceDecorator {


    @Override
    public TraceScope decorator(Trace trace, TraceScope traceScope) {
        // 获取当前上下文
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        Map<String, String> previousMdc = ObjectUtil.isEmpty(copyOfContextMap) ? Collections.EMPTY_MAP : copyOfContextMap;
        // set trace
        if (Objects.nonNull(trace)) {
            MDC.put(LinkKeyConst.getTraceIdKey(), trace.getTraceId());
            if (!trace.getExtraMap().isEmpty()) {
                trace.getExtraMap().forEach(MDC::put);
            }
        } else {
            MDC.remove(LinkKeyConst.getTraceIdKey());
        }
        return () -> {
            // 在释放资源之前释放还需要在拿出来
            Trace currentContext = TraceContext.getCurrentContext();
            // 保存上文
            traceScope.close();
//            // 保存trace
//            replace(LinkKeyConst.TRACE_ID, previousMdc.get(LinkKeyConst.TRACE_ID));
//            // 额外信息
//            if (Objects.nonNull(currentContext)) {
//                ConcurrentHashMap.KeySetView<String, String> strings = currentContext.getExtraMap().keySet();
//                for (String key : strings) {
//                    replace(key, previousMdc.get(key));
//                }
//            }
//            previousMdc.forEach(this::replace);
        };
    }

    private void replace(String key, String s) {
        if (Objects.isNull(s)) {
            MDC.remove(key);
        } else {
            MDC.put(key, s);
        }
    }

    public static void main(String[] args) {
        Collections.emptyMap().forEach((k, v) -> {
            System.out.println(11);
        });
    }
}
