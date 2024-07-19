package org.lkg.redis.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lkg.security.KernelUtil;
import org.lkg.simple.JacksonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/18 5:20 PM
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "redis.more")
@ConditionalOnProperty(value = "redis.more.enable", havingValue = "1", matchIfMissing = false)
public class MoreRedisDataSourceConfig {

    private Integer enable;

    private Map<String, RedisPoolConfig> config;
    private static final String FEATURE_REDIS_NAME = "feature-redis";
    private static final String ORDER_REDIS_NAME = "order-redis";

    @PostConstruct
    public void init() {
        log.info(">>> load more redis config:{} <<<", JacksonUtil.writeValue(config));
    }

    @Data
    private static class RedisPoolConfig {
        // remote
        private Duration readTimeOut = Duration.ofSeconds(1);
        private Duration connectionTimeOut = Duration.ofSeconds(1);

        // basic
        private String host;
        private int port = 6379;
        private String password;
        // pool
        private int minIdle = KernelUtil.CPU_CORE_NUM >> 1;
        private int maxIdle = KernelUtil.CPU_CORE_NUM;
        private int maxConnection = KernelUtil.CPU_CORE_NUM << 1;
        private Duration maxWait = Duration.ofSeconds(1);
        private int database = 0;

    }

    @Bean(FEATURE_REDIS_NAME)
//    @Primary // 解决多个同类型一次性注入的NoUniqueBeanDefinitionException问题
    public RedisTemplate<String, Object> featureRedisTemplate() {
        return getRedisTemplateWithKey(FEATURE_REDIS_NAME);
    }


    @Bean(ORDER_REDIS_NAME)
    public RedisTemplate<String, Object> orderRedisTemplate() {
        return getRedisTemplateWithKey(ORDER_REDIS_NAME);
    }


    private RedisTemplate<String, Object> getRedisTemplateWithKey(String key) {
        RedisPoolConfig redisPoolConfig = config.get(key);
        Assert.notNull(redisPoolConfig, "not loss redis config for:" + key);
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setValueSerializer(valueSerializer());
        redisTemplate.setKeySerializer(keySerializer());
        redisTemplate.setHashKeySerializer(keySerializer());
        redisTemplate.setHashValueSerializer(valueSerializer());
        redisTemplate.setConnectionFactory(buildRedisConnectionFactory(redisPoolConfig));
        return redisTemplate;
    }

    public RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    public RedisSerializer<Object> valueSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略空值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 开启后就可以进行默认的类型推断，就会开启保留非java基本类型的元数据信息，带来额外的存储成本需要谨慎
        // objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
        // JsonTypeInfo.AS 几个常见设置表现形式
        // Property[default] -> {"@class":"org.lkg.elastic_search.crud.demo.Orders","id":0,"name":"测试wkx","age":3,"fee":["java.math.BigDecimal",10],"startTime":["java.util.Date",1721356409597]}
        // WRAPPER_ARRAY -> ["org.lkg.elastic_search.crud.demo.Orders",{"id":0,"name":"测试wkx","age":3,"fee":["java.math.BigDecimal",10],"startTime":["java.util.Date",1721356608170]}]
        // WRAPPER_OBJECT -> {"org.lkg.elastic_search.crud.demo.Orders":{"id":0,"name":"测试wkx","age":3,"fee":{"java.math.BigDecimal":10},"startTime":{"java.util.Date":1721356708655}}}

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        // 空数据的默认序列化方式 -> JsonTypeInfo.Id.CLASS.getDefaultPropertyName()
        GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }


    public RedisConnectionFactory buildRedisConnectionFactory(RedisPoolConfig redisConfig) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // basic redis config
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        PropertyMapper propertyMapper = PropertyMapper.get();
        propertyMapper.from(redisConfig::getHost).whenHasText().to(redisStandaloneConfiguration::setHostName);
        propertyMapper.from(redisConfig::getPort).when(ref -> ref > 0).to(redisStandaloneConfiguration::setPort);
        propertyMapper.from(redisConfig::getDatabase).when(ref -> ref >= 0).to(redisStandaloneConfiguration::setDatabase);
        propertyMapper.from(RedisPassword.of(redisConfig.getPassword())).whenHasText().to(redisStandaloneConfiguration::setPassword);
        // pool config
        propertyMapper.from(redisConfig::getMinIdle).when(ref -> ref > 0).to(jedisPoolConfig::setMinIdle);
        propertyMapper.from(redisConfig::getMaxIdle).when(ref -> ref > 0).to(jedisPoolConfig::setMaxIdle);
        propertyMapper.from(redisConfig::getMaxConnection).when(ref -> ref > 0).to(jedisPoolConfig::setMaxTotal);
        propertyMapper.from(redisConfig::getMaxWait).whenNonNull().as(Duration::toMillis).to(jedisPoolConfig::setMaxWaitMillis);
        // remote config
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = JedisClientConfiguration.builder();
        propertyMapper.from(redisConfig::getConnectionTimeOut).whenNonNull().to(builder::connectTimeout);
        propertyMapper.from(redisConfig::getReadTimeOut).whenNonNull().to(builder::readTimeout);
        JedisClientConfiguration build = builder.usePooling().poolConfig(jedisPoolConfig).build();

        return new JedisConnectionFactory(redisStandaloneConfiguration, build);
    }

}
