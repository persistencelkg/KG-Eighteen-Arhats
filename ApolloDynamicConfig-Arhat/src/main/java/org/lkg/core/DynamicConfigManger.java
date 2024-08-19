package org.lkg.core;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
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

    public static String getServerName() {
        // spring.application.name  -> appName
        return "";
    }

    public static <T> T getTargetClassConfig(Class<T> classz) {
        return null;
    }

    public static String getConfigValue(String key) {
        return getConfigValue(key, String.class);
    }

    public static <T> T getConfigValue(String key, Class<T> defaultVal) {
        return null;
    }

    public static Integer getInt(String key, Integer defaultVal) {
        return defaultVal;
    }

    public static Integer getInt(String key) {
        return 0;
    }

    public static <T> Set<T> toSet(String key) {
        return new HashSet<>();
    }

    public static Duration initDuration(String key, Consumer<Duration> durationConsumer) {
        // TODO 替换
        return initAndRegistChangeEvent(key, ref ->  Duration.ofSeconds(15), durationConsumer);
    }

    public static <T> T getConfigValueWithDefault(String key, Supplier<T> supplier) {
        return supplier.get();
    }


    public static <T> T initAndRegistChangeEvent(String key, Function<String, T> function, Consumer<T> consumer) {
        T apply = function.apply(key);
        consumer.accept(apply);
        return apply;
    }
}
