package org.lkg.elastic_search.spring;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.lkg.core.DynamicConfigManger;
import org.lkg.elastic_search.config.CustomEsRestClientProperties;
import org.lkg.elastic_search.config.MoreEsClient;
import org.lkg.elastic_search.config.OnEnableMoreEs;
import org.lkg.enums.StringEnum;
import org.lkg.retry.BulkAsyncRetryAble;
import org.lkg.utils.ObjectUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/11 10:20 AM
 */
@Configuration
@OnEnableMoreEs
public class MoreEsClientAutoConfiguration {


    @Autowired(required = false)
    private HttpResponseInterceptor esAsynchttpResponseInterceptor;


    @Autowired(required = false)
    private HttpRequestInterceptor esAsynchttpRequestInterceptor;


    @Bean(MoreEsClient.PRIMARY)
    public RestHighLevelClient primaryRestHighLevelClient(ObjectProvider<MoreEsClient> moreEsClient) {
        return getRestHighLevelClient(moreEsClient.getIfUnique(), MoreEsClient.PRIMARY);
    }

    @Bean(MoreEsClient.SECOND)
    public RestHighLevelClient secondRestHighLevelClient(ObjectProvider<MoreEsClient> moreEsClient) {
        return getRestHighLevelClient(moreEsClient.getIfUnique(), MoreEsClient.SECOND);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "es.retry-config.enable", value = "1", matchIfMissing = true)
    public BulkAsyncRetryAble esRetryAble() {
        return new BulkAsyncRetryAble() {
            @Override
            public String prefix() {
                return "es.retry-config";
            }

            @Override
            public String get(String key) {
                return DynamicConfigManger.getConfigValue(key);
            }
        };
    }


    /** 参考ElasticsearchRestClientConfigurations */
    private RestHighLevelClient getRestHighLevelClient(MoreEsClient moreEsClient, String key) {
        Assert.isTrue(ObjectUtil.isNotEmpty(moreEsClient) && ObjectUtil.isNotEmpty(moreEsClient.getMeta()),"more es config meta not null");
        CustomEsRestClientProperties properties = moreEsClient.getMeta().get(key);
        BasicCredentialsProvider bcp = buildCredential(properties);
        // 这一步是rest low level client
        HttpHost[] hosts = properties.getUris().stream().map(this::createHttpHost).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(hosts);

        PropertyMapper map = PropertyMapper.get();
        // http client 配置
        builder.setRequestConfigCallback((requestConfigBuilder) -> {
            map.from(properties::getConnectionTimeout).whenNonNull().asInt(Duration::toMillis)
                    .to(requestConfigBuilder::setConnectTimeout);
            map.from(properties::getReadTimeout).whenNonNull().asInt(Duration::toMillis)
                    .to(requestConfigBuilder::setSocketTimeout);
            return requestConfigBuilder;
        });
//        builder.setMaxRetryTimeoutMillis(2); 控制客户端等待线程的响应时间 而非服务端的响应时间，默认30s
        // 构建rest high level client
        RestClientBuilder restClientBuilder = builder.setHttpClientConfigCallback(
                (HttpAsyncClientBuilder httpAsyncClientBuilder) -> {
                    httpAsyncClientBuilder.setDefaultCredentialsProvider(bcp);
                    map.from(properties::getMaxConnectionPerRoute).whenNonNull().when(ref -> ref > 0).to(httpAsyncClientBuilder::setMaxConnPerRoute);
                    map.from(properties::getMaxConnectionTotal).whenNonNull().when(ref -> ref > 0).to(httpAsyncClientBuilder::setMaxConnTotal);
//                    httpAsyncClientBuilder.setKeepAliveStrategy(((response, context) -> TimeUnit.MINUTES.toMillis(3)));
                    // 底层通过周期性检测超时时间，默认周期1s; 所以自测时会不稳定出现：setSelectInterval
                    // setSoTimeout 是网络层的配置，而上面requestConfig的readTimeout是Http应用层配置，控制单个http请求读取超时
                    IOReactorConfig build = IOReactorConfig.custom().setSelectInterval(properties.getCheckTimoutInterval()).build();
                    httpAsyncClientBuilder.setDefaultIOReactorConfig(build);
                    // 拦截器 http processor 在AsyncHttpBuilder请求时请求头格式即便设置了application/json 返回值也是text/html

                    httpAsyncClientBuilder.addInterceptorLast(esAsynchttpRequestInterceptor);
                    httpAsyncClientBuilder.addInterceptorFirst(esAsynchttpResponseInterceptor);

                    return httpAsyncClientBuilder;
                });


        return new RestHighLevelClient(restClientBuilder);
    }


    private HttpHost createHttpHost(String s) {
        return createHttpHost(URI.create(s));
    }
    private HttpHost createHttpHost(URI uri) {
        if (!StringUtils.hasLength(uri.getUserInfo())) {
            return HttpHost.create(uri.toString());
        }
        try {
            return HttpHost.create(new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(),
                    uri.getQuery(), uri.getFragment()).toString());
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }


    /**
     * uri 有两种形式，一种是普通的ip:port
     * 还有一种是：将username、password整合到uri上，例如
     * jdbc:mysql://user:password@host:port/path
     */


    public BasicCredentialsProvider buildCredential(ElasticsearchRestClientProperties properties) {
        BasicCredentialsProvider bcp = new BasicCredentialsProvider();
        if (StringUtils.hasText(properties.getUsername())) {
            bcp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword()));
        }
        properties.getUris().stream().map(URI::create)
                .filter(this::hasUserInfo)
                .forEach(ref -> addUserInfoCredentials(ref, bcp));
        return bcp;
    }



    private boolean hasUserInfo(URI uri) {
        return uri != null && StringUtils.hasLength(uri.getUserInfo());
    }

    private void addUserInfoCredentials(URI uri, BasicCredentialsProvider bcp) {
        AuthScope authScope = new AuthScope(uri.getHost(), uri.getPort());
        Credentials credentials = createUserInfoCredentials(uri.getUserInfo());
        bcp.setCredentials(authScope, credentials);
    }

    private Credentials createUserInfoCredentials(String userInfo) {
        int delimiter = userInfo.indexOf(StringEnum.COLON);
        if (delimiter == -1) {
            return new UsernamePasswordCredentials(userInfo, null);
        }
        String username = userInfo.substring(0, delimiter);
        String password = userInfo.substring(delimiter + 1);
        return new UsernamePasswordCredentials(username, password);
    }
}
