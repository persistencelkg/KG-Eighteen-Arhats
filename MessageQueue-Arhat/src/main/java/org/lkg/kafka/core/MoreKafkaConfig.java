package org.lkg.kafka.core;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.requests.ProduceRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/27 3:24 PM
 */
@ConditionalOnClass(KafkaProperties.class)
@ConfigurationProperties(prefix = "more")
@Component
@Data
public class MoreKafkaConfig {
    public static final String first = "primaryKafka";
    public static final String second = "secondKafka";

    private Map<String, KafkaProperties> kafka;
}
