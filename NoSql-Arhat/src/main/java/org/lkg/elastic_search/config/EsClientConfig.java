package org.lkg.elastic_search.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.lkg.simple.ObjectUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/13 3:51 PM
 */

@Configuration
@ConfigurationProperties(prefix = "es-config")
@ConditionalOnClass(RestHighLevelClient.class)
@Slf4j
public class EsClientConfig {
    private Map<String, EsConfigMeta> meta;
    public static int defaultEsIndexNumOfShards = 3;
    public static int defaultEsIndexNumanReplica = 1;


    @PostConstruct
    public void initEsClientConfig() {
        if (ObjectUtil.isEmpty(meta)) {
            return;
        }
        meta.forEach((k, v) -> getRestHighLevelClient(v));
        log.info("has loading es config meta:{}", meta.values());
    }


    private RestHighLevelClient getRestHighLevelClient(EsConfigMeta group) {
        String[] clientIpList = group.getHost().split(",");
        HttpHost[] httpHosts = new HttpHost[clientIpList.length];
        for (int i = 0; i < clientIpList.length; i++) {
            httpHosts[i] = HttpHost.create(clientIpList[i]);
        }
        BasicCredentialsProvider bcp = new BasicCredentialsProvider();
        if (!ObjectUtil.isEmpty(group.userName) && !ObjectUtil.isEmpty(group.getPassword())) {
            bcp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(group.userName, group.password));
        }
        // 这一步是rest low level client
        RestClientBuilder builder = RestClient.builder(httpHosts);
        PropertyMapper map = PropertyMapper.get();
        builder.setRequestConfigCallback((requestConfigBuilder) -> {
            map.from(group::getConnectionTimeOut).whenNonNull().asInt(Math::toIntExact)
                    .to(requestConfigBuilder::setConnectTimeout);
            map.from(group::getSocketTimeout).whenNonNull().asInt(Math::toIntExact)
                    .to(requestConfigBuilder::setSocketTimeout);
            return requestConfigBuilder;
        });
        // 构建rest high level client
        return new RestHighLevelClient(builder.setHttpClientConfigCallback(
                (HttpAsyncClientBuilder httpAsyncClientBuilder) -> {
                    httpAsyncClientBuilder.setDefaultCredentialsProvider(bcp);
                    return httpAsyncClientBuilder;
                }));

    }

    @Data
    public static class EsConfigMeta {
        private String host;
        private String userName;
        private String password;
        private Integer retryCount;
        private Integer retrySleep;
        private long socketTimeout;
        private long connectionTimeOut;
    }
}

