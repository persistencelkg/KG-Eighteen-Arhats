package org.lkg.core;

import org.lkg.simple.ObjectUtil;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;

/**
 * Description: 可扩展其他参数到链路的注入器，支持动态配置，例如全链路压测标识
 * Author: 李开广
 * Date: 2024/9/23 9:40 PM
 */
public interface ExtraEntryInjector {

    void inject(String key, String value);

    void remove(String key);

    Trace populateExtra(Trace trace);

    class DefaultInjector implements ExtraEntryInjector {

        private final Map<String, String> dynamicExtraEntry;

        public DefaultInjector() {
            dynamicExtraEntry = DynamicConfigManger.toMap("full-link-entry", String.class, String.class);
        }

        @Override
        public void inject(String key, String value) {
            if (TraceHolder.existCurrentContext()) {
                if (Objects.nonNull(key) && Objects.nonNull(value)) {
                    MDC.put(key, value);
                } else if (Objects.nonNull(key)) {
                    remove(key);
                }
            }
        }

        @Override
        public void remove(String key) {
            MDC.remove(key);
        }

        @Override
        public Trace populateExtra(Trace trace) {
            if (ObjectUtil.isNotEmpty(dynamicExtraEntry)) {
                dynamicExtraEntry.forEach(trace::addExtra);
            }
            return trace;
        }
    }
}
