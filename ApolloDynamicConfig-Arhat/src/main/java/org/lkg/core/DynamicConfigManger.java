package org.lkg.core;

import org.lkg.apollo.ApolloConfigService;
import org.lkg.enums.StringEnum;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;

import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/8 4:18 PM
 */
public class DynamicConfigManger {

    // 不直接初始化的原因是，使用了ApplicationContextInitializer 注入configService 但是如果此时初始化了ApolloConfigService
    // 会在容器之初加载apollo核心配置，但是这个时候核心配置都还没有准备好，获取的一定是null，而走到默认值，甚至app-id 都获取不到
    private static final ApolloConfigService apolloConfigService = null;

    private static final Map<String, Object> LOCAL_CACHE = new HashMap<>();
    private static final List<Function<String, String>> FILTERS = new ArrayList<>();
    // 未来如果要支持其他配置中心的动态key change, 例如nacos，但是需要注意nacos需要单独一个模块切记不能将 可以通过下面方式，目前apollo挺好的
    private static final Set<KeyConfigService> KEY_CONFIG_SERVICE_SET = new HashSet<>(4);
    public static void registerConfigService(KeyConfigService configService) {
        KEY_CONFIG_SERVICE_SET.add(configService);
    }

    public static void addValueFilter(Function<String, String> func) {
        FILTERS.add(func);
    }


    public static String getEnv() {
        // spring.profile.active -> env
        return ApolloConfigService.getInstance().getEnv();
    }

    public static String getServerName() {
        // spring.application.name  -> appName
        return getConfigValue("spring.application.name", getConfigValue("app.id"));
    }

    public static <T> T getAnnotationConfig(Class<T> clz) {
        DynamicKeyConfig annotation = clz.getAnnotation(DynamicKeyConfig.class);
        if (ObjectUtil.isEmpty(annotation)) {
            return null;
        }
        String configValue = getConfigValue(annotation.key());
        return JacksonUtil.readValue(configValue, clz);
    }

    public static String getConfigValue(String key) {
        return getConfigValue(key, null);
    }

    public static Duration getDuration(String key, Duration defVal) {
        String configValue = getConfigValue(key);
        return Optional.ofNullable(AdvanceFunctions.STR_TO_DURATION.apply(configValue)).orElse(defVal);
    }

    public static String getConfigValue(String key, String def) {
        String strValue = null;
        for (KeyConfigService keyConfigService : KEY_CONFIG_SERVICE_SET) {
            strValue = keyConfigService.getStrValue(key, def);
            // 解决值本身是占位符的问题
            if (ObjectUtil.isNotEmpty(strValue)) {
                for (Function<String, String> filter : FILTERS) {
                    strValue = filter.apply(strValue);
                }
                break;
            }
        };

        return Optional.ofNullable(strValue).orElse(def);
    }

    public static Integer getInt(String key, Integer defaultVal) {
        String configValue = getConfigValue(key);
        return Optional.ofNullable(AdvanceFunctions.TO_INT_FUNCTION.apply(configValue)).orElse(defaultVal);
    }

    public static Integer getInt(String key) {
        return getInt(key, null);
    }


    public static Long getLong(String key, Long def) {
        String configValue = getConfigValue(key);
        return Optional.ofNullable(AdvanceFunctions.TO_LONG_FUNCTION.apply(configValue)).orElse(def);
    }

    public static Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static Boolean getBoolean(String key, boolean def) {
        String configValue = getConfigValue(key);
        return Optional.ofNullable(AdvanceFunctions.TO_BOOLEAN_FUNCTION.apply(configValue)).orElse(def);
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

    public static Duration initDuration(String key, Duration defaultVal, Consumer<Duration> durationConsumer) {
        return initAndRegistChangeEvent(key, ref -> getDuration(key, defaultVal), durationConsumer);
    }

    public static <T> T getConfigValueWithDefault(String key, Supplier<T> supplier) {
        String configValue = getConfigValue(key, String.valueOf(supplier.get()));
        ParameterizedType paramClass = ((ParameterizedType) supplier.getClass().getGenericInterfaces()[0]);
        Class<T> componentType = (Class<T>) paramClass.getActualTypeArguments()[0].getClass();
        return JacksonUtil.readValue(configValue, componentType);
    }


    public static <T> T initAndRegistChangeEvent(String key, Function<String, T> function, Consumer<T> consumer) {
        KeyChangeHandler changeHandler = keyChange -> consumer.accept(function.apply(key));
        addKeyChangeHandler(key, changeHandler);
        T apply = function.apply(key);
        consumer.accept(apply);
        return apply;
    }

    private static void addKeyChangeHandler(String key, KeyChangeHandler changeHandler) {
        ApolloConfigService.getInstance().addChangeKeyPostHandler(key, changeHandler);
    }

    public static void addKeyChangeHandler(String key, Supplier<?> supplier) {
        // 不依赖key
        addKeyChangeHandler(key, ref -> supplier.get());
    }

    public static void main(String[] args) {
        StringJoiner stringJoiner = new StringJoiner(StringEnum.EMPTY, StringEnum.LEFT_SQ_BRACKET, StringEnum.RIGHT_SQ_BRACKET);
        stringJoiner.add("23,34");
        System.out.println(Duration.parse("PT10S"));
    }
}
