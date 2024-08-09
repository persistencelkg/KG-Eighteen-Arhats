package org.lkg.redis.crud;

import lombok.extern.slf4j.Slf4j;
import org.lkg.redis.config.RedisTemplateHolder;
import org.lkg.simple.FileUtil;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

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

    public static final String DYNAMIC_UPDATE_BY_LUA = "dynamic_update_hash_by_lua.lua";

    private static final Map<String, String> LUA_MAP = new HashMap<>();

    static {
        try {
            String s = FileUtil.readFile(RedisService.class.getClassLoader().getResourceAsStream("lua" + File.separator + DYNAMIC_UPDATE_BY_LUA));
            LUA_MAP.put(DYNAMIC_UPDATE_BY_LUA, s);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }


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


    @Nullable
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

    public long execWithLua(String luaFileName, String key, Map<String, Object> map) {
        if (ObjectUtil.isEmpty(map)) {
            return 0L;
        }
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<Object> valList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            valList.add(entry.getKey());
            valList.add(entry.getValue());
        }
        Object[] array = valList.toArray();
        Long aLong = executeFunction((template, baseKey) -> {
                    keyList.add(baseKey);
                    // 尽量不要对参数做定制化序列化，因为参数可能类型不一致
                    return template.execute(
                            new DefaultRedisScript<>(LUA_MAP.get(luaFileName), Long.class),
                            keyList,
                            array);
                },
                key, "execWithLua");
        return Optional.ofNullable(aLong).orElse(0L);
    }

    public List<Object> pipeLine(String key) {

//        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
//            @Override
//            public Object execute(RedisOperations operations) throws DataAccessException {
//                // pTtl
//                operations.getExpire(key, TimeUnit.SECONDS);
//                // get origin key
//                operations.opsForValue().get(key);
//                return null;
//            }
//        };
//
//        List<Object> results = featureRedisTemplate.executePipelined(sessionCallback);

        return executeFunction((template, baseKey) -> template.executePipelined(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.ttl(baseKey.getBytes());
                connection.get(baseKey.getBytes());
                return null;
            }
        }), key, "pipeLine");
    }


    public long ttl(String key) {
        Long expire = executeFunction(RedisTemplate::getExpire, key, "ttl");
        return Optional.ofNullable(expire).orElse(0L);
    }

    public void getLockWithAutoRelease(Supplier<Boolean> consumer, String key, long second) {
        getLockWithAutoRelease(consumer, key, "locked", second);
    }

    public void getLockWithAutoRelease(Supplier<Boolean> consumer, String key, Object val, long second) {
        boolean lock = false;
        try {
            lock = getLock(key, val, second);
            if (lock) {
                if (consumer.get()) {
                    log.debug("success get lock:{} and finish operation", key);
                } else {
                    log.warn("success get lock:{} but exec fail!", key);
                }
            } else {
                log.warn("fail get lock:{}", key);
            }
        } finally {
            if (lock) {
                delKey(key);
            }
        }
    }

}
