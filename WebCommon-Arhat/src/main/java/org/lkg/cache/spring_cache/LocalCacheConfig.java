package org.lkg.cache.spring_cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/23 9:56 PM
 */

@Configuration
@EnableCaching // 开启缓存注入
@ConfigurationProperties(prefix = LocalCacheConfig.PREFIX_LOCAL_CACHE)
@ConditionalOnClass(Caffeine.class)
@ConditionalOnProperty(value = "feature.local-cache-switch", havingValue = "1", matchIfMissing = true)
public class LocalCacheConfig {

    private final static String FEATURE_CACHE_MANAGER = "simpleCacheManager";
    private Map<String, FeatureCache> config;

    final static String PREFIX_LOCAL_CACHE = "feature.cache";

    @Data
    public static class FeatureCache {
        private Duration expireAfterWrite;
        private Duration expireAfterAccess;
        private Short maxSize;
        private Short initCapacity;
    }

    @Bean(name = FEATURE_CACHE_MANAGER)
    public SimpleCacheManager simpleCacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(buildCaffeineCache());
        return simpleCacheManager;
    }

    private Collection<CaffeineCache> buildCaffeineCache() {
        Set<CaffeineCache> set = new HashSet<>();
        if (CollectionUtils.isEmpty(config)) {
            throw new RuntimeException("your open cache，but lose config for key:" + PREFIX_LOCAL_CACHE);
        }
        addCache(set, config);
        return set;
    }

    private void addCache(Set<CaffeineCache> set, Map<String, FeatureCache> featureCacheMap) {
        featureCacheMap.forEach((k, v) -> {
            Cache<Object, Object> build = Caffeine.newBuilder()
                    .expireAfterWrite(v.getExpireAfterWrite())
//                            .expireAfterAccess(globalLocal.getExpireAfterAccess())
                    .maximumSize(v.getMaxSize())
                    .initialCapacity(v.getInitCapacity())
                    .build();
            CaffeineCache caffeineCache = new CaffeineCache(k, build);
            set.add(caffeineCache);
        });
    }


}
