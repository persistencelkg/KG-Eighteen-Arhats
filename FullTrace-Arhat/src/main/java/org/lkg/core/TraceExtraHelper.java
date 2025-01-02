package org.lkg.core;

import org.checkerframework.checker.units.qual.C;
import org.lkg.simple.ObjectUtil;

import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/29 9:23 PM
 */
public class TraceExtraHelper {

    public static String getExtra(String key) {
        Trace currentContext = TraceContext.getCurrentContext();
        if (Objects.nonNull(currentContext)) {
            return currentContext.getExtra(key);
        }
        return null;
    }

    public static String addExtra(String key, String value) {
        Trace currentContext = TraceContext.getCurrentContext();
        if (Objects.nonNull(currentContext)) {
            currentContext.addExtra(key, value);
            return value;
        }
        return null;
    }

    public static <C> String addExtra(FullLinkPropagation.Getter<C, String> getter, C carrier, String key) {
        if (ObjectUtil.isEmpty(getter) || ObjectUtil.isEmpty(key)) {
            return null;
        }
        String value = getter.get(carrier, key);
        return addExtra(key, value);
    }


    public static boolean removeExtra(String key) {
        TraceHolder instance = TraceHolder.getInstance();
        if (Objects.nonNull(instance) ) {
            instance.getEntryInjector().remove(key);
            Trace current = TraceHolder.getCurrent();
            if (Objects.nonNull(current)) {
                current.removeExtra(key);
            }
            return true;
        }
        return false;
    }
}
