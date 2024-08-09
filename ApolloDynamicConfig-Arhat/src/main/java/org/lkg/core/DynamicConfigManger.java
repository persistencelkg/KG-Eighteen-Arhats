package org.lkg.core;

import java.util.function.Supplier;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/8 4:18 PM
 */
public class DynamicConfigManger {


    public static <T> T getConfigValue(Class<T> classz) {
        return null;
    }
    public static String getConfigValue(String key) {
        return getConfigValue(key, null);
    }

    public static String getConfigValue(String key, String defaultVal) {
        return "";
    }

    public static <T> T getConfigValueWithDefault(String key, Supplier<T> supplier) {
        return supplier.get();
    }
}
