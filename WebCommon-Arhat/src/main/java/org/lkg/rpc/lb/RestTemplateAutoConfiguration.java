package org.lkg.rpc.lb;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.internal.connection.RealConnectionPool;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.lkg.enums.TrueFalseEnum;
import org.lkg.utils.http.CustomWebClientConfig;
import org.lkg.utils.http.httpclient.HttpClientUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/22 5:23 PM
 */
@Configuration
public class RestTemplateAutoConfiguration {

    @Bean
    public RestTemplate restTemplate(ObjectProvider<ClientHttpRequestFactory> objectProvider) {
        ClientHttpRequestFactory ifAvailable = objectProvider.getIfAvailable();
        if (Objects.isNull(ifAvailable)) {
            return new RestTemplate();
        }
        RestTemplate restTemplate = new RestTemplate(ifAvailable);
        // 解决中文乱码问题
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(OkHttpClient okHttpClient) {
        HttpClient httpClient = HttpClients.custom()
                .setConnectionTimeToLive(5000, TimeUnit.MILLISECONDS)
                .build();

//        return new HttpComponentsClientHttpRequestFactory(httpClient);
//        return new OkHttp3ClientHttpRequestFactory(okHttpClient);

        return new HttpComponentsClientHttpRequestFactory(HttpClientUtil.getHttpClientWithPool(null, new CustomWebClientConfig.CommonHttpClientConfig()));
    }

    @Bean
    @ConfigurationProperties(prefix = "okhttp.custom.config")
    public CustomWebClientConfig okHttpCustomConfig() {
        return new CustomWebClientConfig();
    }

    @Bean
    public OkHttpClient okHttpClient(CustomWebClientConfig customWebClientConfig) {
        CustomWebClientConfig.CommonHttpClientConfig global = customWebClientConfig.getGlobal();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (Objects.isNull(global)) {
            // 默认10s
           return builder.build();
        }
        if (TrueFalseEnum.isTrue(global.getUsePool())) {
            builder.connectionPool(new ConnectionPool(global.getMaxPerRoute(), 1, TimeUnit.MINUTES));
        }
//        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        builder.readTimeout(global.getSocketTimeOut(), TimeUnit.MILLISECONDS);
        builder.connectTimeout(global.getConnectionTimeOut(), TimeUnit.MILLISECONDS);
        builder.retryOnConnectionFailure(TrueFalseEnum.isTrue(global.getRetryFlag()));
        return builder.build();
    }


}
