package org.lkg.redis.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/19 2:39 PM
 */
@Component
public class RedisTemplateHolder {


    @Resource
    private Map<String, RedisTemplate<String, Object>> redisTemplateMap;


    public RedisTemplate<String, Object> featureTemplate() {
        return redisTemplateMap.get("feature-redis");
    }


    public RedisTemplate<String, Object> orderTemplate() {
        return redisTemplateMap.get("order-redis");
    }
}
