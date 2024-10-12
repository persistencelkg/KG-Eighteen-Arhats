package org.lkg.elastic_search.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

/**
 * Description: 多ES方案
 * Author: 李开广
 * Date: 2024/10/11 10:24 AM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnClass(value = {MoreEsClient.class, RestHighLevelClient.class})
@ConditionalOnProperty(value = "es-config.enable", havingValue = "1", matchIfMissing = true)
public @interface OnEnableMoreEs {
}
