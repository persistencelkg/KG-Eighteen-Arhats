package org.lkg.core;

import org.lkg.constant.LinkKeyConst;
import org.lkg.simple.ObjectUtil;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Description: 可扩展其他参数到链路的注入器，支持动态配置，例如全链路压测标识
 * Author: 李开广
 * Date: 2024/9/23 9:40 PM
 */
public interface ExtraEntryInjector {


    static ExtraEntryInjector DEFAULT = new DefaultInjector();

    void remove(String key);

    Trace populateExtra(Trace trace);

    class DefaultInjector implements ExtraEntryInjector {

        private Map<String, String> dynamicExtraEntry;

        private DefaultInjector() {
            dynamicExtraEntry = DynamicConfigManger.initAndRegistChangeEvent(LinkKeyConst.CUSTOM_FULL_LINK_KEY, DynamicConfigManger::toMap, this::refresh);
        }

        private void refresh(Map<String, String> map) {
            if (!ObjectUtil.isEmpty(dynamicExtraEntry)) {
                // 避免泄露
                dynamicExtraEntry.keySet().forEach(MDC::remove);
            }
            if (ObjectUtil.isNotEmpty(map)) {
                dynamicExtraEntry = new HashMap<>(map);
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
