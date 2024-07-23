package org.lkg.redis.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/19 2:39 PM
 */
@Component
public class RedisTemplateHolder {


    @Resource
    private Map<String, RedisTemplate<String, Object>> redisTemplateMap;

    @Resource
    private MoreRedisDataSourceConfig moreRedisDataSourceConfig;


    public RedisTemplate<String, Object> featureTemplate() {
        return redisTemplateMap.get(MoreRedisDataSourceConfig.FEATURE_REDIS_NAME);
    }


    public RedisTemplate<String, Object> orderTemplate() {
        return redisTemplateMap.get(MoreRedisDataSourceConfig.ORDER_REDIS_NAME);
    }

    public String keyPrefix(RedisTemplate<String, Object> template) {
        Map.Entry<String, RedisTemplate<String, Object>> entry = redisTemplateMap.entrySet().stream().filter((ref) -> Objects.equals(template, ref.getValue())).findFirst().orElse(null);
        if (Objects.isNull(entry)) {
            return "";
        }
        return moreRedisDataSourceConfig.getConfig().get(entry.getKey()).getKeyPrefix();
    }
}
