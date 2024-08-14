package org.lkg.core;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/8 4:18 PM
 */
public class DynamicConfigManger {


    public static String getEnv() {
        // spring.profile.active -> env
        return null;
    }

    public static String getServerName(){
        // spring.application.name  -> appName
        return "";
    }

    public static <T> T getConfigValue(Class<T> classz) {
        return null;
    }
    public static String getConfigValue(String key) {
        return getConfigValue(key, (String) null);
    }

    public static String getConfigValue(String key, String defaultVal) {
        return "";
    }

    public static Integer getConfigValue(String key, Integer defaultVal) {
        return defaultVal;
    }

    public static <T> T getConfigValueWithDefault(String key, Supplier<T> supplier) {
        return supplier.get();
    }


    public static <T> T initAndRegistChangeEvent(String key, Function<String, T> function, Consumer<T> consumer) {
        return null;
    }
}
