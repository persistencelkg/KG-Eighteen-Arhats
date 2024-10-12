package org.lkg.elastic_search.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.lkg.simple.BeanUtil;
import org.lkg.simple.ObjectUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/13 3:51 PM
 */

@Component
@ConfigurationProperties(prefix = "es-config")
@Slf4j
public class MoreEsClient {


    public static final String PRIMARY = "order";
    public static final String SECOND = "second";

    public static int defaultEsIndexNumOfShards = 3;
    public static int defaultEsIndexNumanReplica = 1;


    @Getter
    @Setter
    private Integer enable;

    @Getter
    @Setter
    private Map<String, CustomEsRestClientProperties> meta;


    @PostConstruct
    public void initEsClientConfig() {
        // init config
        log.info("has loading es config meta:{}", meta.values());
    }
}

