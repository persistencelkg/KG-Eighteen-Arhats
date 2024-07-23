package org.lkg.redis.crud;

import lombok.extern.slf4j.Slf4j;
import org.lkg.redis.config.RedisTemplateHolder;
import org.lkg.simple.JacksonUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/19 2:07 PM
 */
@Service
@Slf4j
public class RedisService {

    @Resource
    private RedisTemplateHolder redisTemplateHolder;

    // 锁
    public boolean getLock(String key, Object val, long second) {
        Boolean aBoolean = executeFunction((template, baseKey) -> template.opsForValue().setIfAbsent(baseKey, val, second, TimeUnit.SECONDS), key, "getLock");
        return Boolean.TRUE.equals(aBoolean);
    }

    public void setKeyWithSecond(String key, Object val, long second) {
        setKey(key, val, second, TimeUnit.SECONDS);
    }

    public void setKey(String key, Object val, long num, TimeUnit timeUnit) {
        executeConsumer((template, baseKey) -> template.opsForValue().set(baseKey, val, num, timeUnit), key, "delKey");
    }

    public <T> T getKey(String key, Class<T> tClass) {
        return executeFunction((template, baseKey) -> {
            Object o = template.opsForValue().get(baseKey);
            return JacksonUtil.getMapper().convertValue(o, tClass);
        }, key, "getKey");
    }

    public boolean delKey(String key) {
        Boolean aBoolean = executeFunction(RedisTemplate::delete, key, "delKey");
        return Boolean.TRUE.equals(aBoolean);
    }

    // 计数

    public long autoInc(String key) {
        return inc(key, 1);
    }

    public long autoDec(String key) {
        return inc(key, -1);
    }

    public long inc(String key, int val) {
        Long inc = executeFunction(((template, baseKey) -> Optional.ofNullable(template.opsForValue().increment(baseKey, val)).orElse(0L)), key, "inc");
        return Optional.ofNullable(inc).orElse(0L);
    }

    // 统计计数
    public long hyperLog(String key, Object... val) {
        Long hyperLog = executeFunction((template, baseKey) -> template.opsForHyperLogLog().add(baseKey, val), key, "hyperLog");
        return Optional.ofNullable(hyperLog).orElse(0L);
    }


    public <T> T hGet(String key, String field, Class<T> tClass) {
        Object o = hGet(key, field);
        if (Objects.nonNull(o)) {
            return JacksonUtil.getMapper().convertValue(o, tClass);
        }
        return null;
    }

    public Object hGet(String key, String field) {
        return executeFunction((template, baseKey) -> template.opsForHash().get(baseKey, field), key, "hGet");
    }


    public void hSet(String key, String field, Object val) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(field, val);
        hSet(key, map);
    }

    public void hSet(String key, Map<String, Object> map) {
        executeConsumer((template, baseKey) -> template.opsForHash().putAll(baseKey, map), key, "hSet");
    }

    public Long hDel(String key, Object... field) {
        Long aLong = executeFunction((template, baseKey) -> Optional.ofNullable(template.opsForHash().delete(baseKey, field)).orElse(0L), key, "hDel");
        return Optional.ofNullable(aLong).orElse(0L);
    }


    private <T> T executeFunction(BiFunction<RedisTemplate<String, Object>, String, T> supplier, String key, String methodNameToLog) {
        // 后续可以进行数据源切换
        RedisTemplate<String, Object> redisTemplate = redisTemplateHolder.featureTemplate();
        String keyPrefix = redisTemplateHolder.keyPrefix(redisTemplate);
        String finalKey = keyPrefix + key;
        try {
            return supplier.apply(redisTemplate, finalKey);
        } catch (Exception e) {
            log.error("{}:{} exec fail:{}", methodNameToLog, finalKey, e.getMessage(), e);
        }
        return null;
    }

    private void executeConsumer(BiConsumer<RedisTemplate<String, Object>, String> consumer, String baseKey, String methoNameToLog) {
        RedisTemplate<String, Object> redisTemplate = redisTemplateHolder.featureTemplate();
        String keyPrefix = redisTemplateHolder.keyPrefix(redisTemplate);
        String finalKey = keyPrefix + baseKey;
        try {
            consumer.accept(redisTemplate, finalKey);
        } catch (Exception e) {
            log.error("{}:{} exec fail:{}", methoNameToLog, finalKey, e.getMessage(), e);
        }
    }
}
