package org.lkg.rocketmq.core;

import lombok.Data;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/30 4:49 PM
 */
@Data
@Component
@ConditionalOnClass(RocketMQProperties.class)
@ConfigurationProperties(prefix = "more.rocketmq")
public class MoreRocketMqConfig {

    public static final String primary = "primaryRocket";
    public static final String second = "secondRocket";

    private Map<String, RocketMQProperties> config;

}
