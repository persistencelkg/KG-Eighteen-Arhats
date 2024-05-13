package org.lkg.elastic_search.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/13 3:51 PM
 */

@Configuration
@ConditionalOnClass(RestHighLevelClient.class)
public class EsClientConfig {
}

