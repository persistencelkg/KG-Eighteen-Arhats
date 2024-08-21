package org.lkg.core;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.util.function.Functions;
import org.lkg.apollo.ApolloConfigService;
import org.lkg.enums.StringEnum;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/8 4:18 PM
 */
public class DynamicConfigManger {


    private static final ApolloConfigService apolloConfigService = new ApolloConfigService();

    // 未来如果要支持其他配置中心的动态key change, 例如nacos，但是需要注意nacos需要单独一个模块切记不能将 可以通过下面方式，目前apollo挺好的
//    private static final Set<DynamicConfigService> list = new HashSet<>(4);
    public static void registerConfigService(DynamicConfigService configService) {
//        list.add(configService);

    }


    public static String getEnv() {
        // spring.profile.active -> env
        return apolloConfigService.getEnv();
    }

    public static String getServerName() {
        // spring.application.name  -> appName
        return getConfigValue("spring.application.name", getConfigValue("app.id"));
    }

    public static <T> T getTargetClassConfig(Class<T> classz) {
        return null;
    }

    public static String getConfigValue(String key) {
        return getConfigValue(key, null);
    }

    public static String getConfigValue(String key, String def) {
        return apolloConfigService.getStrValue(key, def);
    }

    public static Integer getInt(String key, Integer defaultVal) {
        String configValue = getConfigValue(key);
        return Optional.ofNullable(Functions.TO_INT_FUNCTION.apply(configValue)).orElse(defaultVal);
    }

    public static Integer getInt(String key) {
        return getInt(key, null);
    }


    public static Long getLong(String key, Long def) {
        String configValue = getConfigValue(key);
        return Optional.ofNullable(Functions.TO_LONG_FUNCTION.apply(configValue)).orElse(def);
    }

    public static Long getLong(String key) {
        return getLong(key, null);
    }

    public static Set<String> toSet(String key) {
        return toSet(key, String.class);
    }

    public static <T> Set<T> toSet(String key, Class<T> clz) {
        return new HashSet<>(toCollection(key, clz));
    }

    public static List<String> toList(String key) {
        return toList(key, String.class);
    }

    public static <T> List<T> toList(String key, Class<T> clz) {
        return new ArrayList<>(toCollection(key, clz));
    }

    public static Map<String, Object> toMap(String key) {
        return toMap(key, String.class, Object.class);
    }

    public static <K, V> Map<K, V> toMap(String key, Class<K> keyClass, Class<V> valClass) {
        String configValue = getConfigValue(key);
        return JacksonUtil.readMap(configValue, keyClass, valClass);
    }

    public static <T> Collection<T> toCollection(String key, Class<T> clz) {
        String configValue = getConfigValue(key);
        StringJoiner stringJoiner = new StringJoiner(StringEnum.EMPTY, StringEnum.LEFT_SQ_BRACKET, StringEnum.RIGHT_SQ_BRACKET);
        stringJoiner.add(configValue);
        return JacksonUtil.readCollection(stringJoiner.toString(), clz);
    }

    public static Duration initDuration(String key, Consumer<Duration> durationConsumer) {
        // TODO 替换
        return initAndRegistChangeEvent(key, ref -> Duration.ofSeconds(15), durationConsumer);
    }

    public static <T> T getConfigValueWithDefault(String key, Supplier<T> supplier) {
        return supplier.get();
    }


    public static <T> T initAndRegistChangeEvent(String key, Function<String, T> function, Consumer<T> consumer) {
        T apply = function.apply(key);
        consumer.accept(apply);
        return apply;
    }

    public static void main(String[] args) {
        StringJoiner stringJoiner = new StringJoiner(StringEnum.EMPTY, StringEnum.LEFT_SQ_BRACKET, StringEnum.RIGHT_SQ_BRACKET);
        stringJoiner.add("23,34");
    }
}
