package org.lkg.core;

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

    public static boolean addExtra(String key, String value) {
        Trace currentContext = TraceContext.getCurrentContext();
        if (Objects.nonNull(currentContext)) {
            currentContext.addExtra(key, value);
        }
        return false;
    }

    public static boolean removeExtra(String key) {
        TraceHolder instance = TraceHolder.getInstance();
        if (Objects.nonNull(instance)) {
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
