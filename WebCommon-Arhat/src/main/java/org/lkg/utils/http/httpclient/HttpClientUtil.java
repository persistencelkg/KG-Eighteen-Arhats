package org.lkg.utils.http.httpclient;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.lkg.enums.TrueFalseEnum;
import org.lkg.request.CommonResp;
import org.lkg.request.InternalRequest;
import org.lkg.request.InternalResponse;
import org.lkg.simple.JacksonUtil;
import org.lkg.utils.http.CustomWebClientConfig;
import org.lkg.utils.http.CustomWebClientHolder;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description: 基于HTTP-client 自带重试请求
 * Author: 李开广
 * Date: 2024/7/1 11:11 AM
 */
@Slf4j
public class HttpClientUtil {

    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    public static InternalResponse invoke(String serverName, InternalRequest request) {
        CustomWebClientConfig customWebClientConfig = CustomWebClientHolder.getCustomWebClientConfig();
        StopWatch stop = new StopWatch();
        stop.start(serverName);
        CustomHttpRequest customHttpRequest = new CustomHttpRequest(request, serverName);
        // head 准备
        customHttpRequest.getInternalRequest().getHeaders().forEach(customHttpRequest::addHeader);
        InternalResponse internalResponse = executeHttpRequest(customHttpRequest, customWebClientConfig);
        stop.stop();
        // 日志记录
        internalResponse.setCostTime(stop.getTotalTimeMillis());
        return internalResponse;
    }

    public static <T, C> CommonResp<T,C> convertToCommon(InternalResponse internalResponse) {
        return JacksonUtil.readObj(internalResponse.getResult(), new TypeReference<CommonResp<T, C>>() {});
    }

    public static void main(String[] args) {
        InternalRequest postRequest = InternalRequest.createPostRequest("ttet", InternalRequest.BodyEnum.RAW);
        InternalResponse internalResponse = new InternalResponse(null);
        internalResponse.setResult("{\"code\": 11, \"data\":{\"test\": 1}}");
        CommonResp<Integer, Object> objectObjectCommonResp = convertToCommon(internalResponse);
        System.out.println(objectObjectCommonResp);
    }

    private static InternalResponse executeHttpRequest(CustomHttpRequest uriRequest, CustomWebClientConfig config) {
        InternalRequest internalRequest = uriRequest.getInternalRequest();
        InternalResponse result = new InternalResponse(internalRequest);
        CloseableHttpResponse response = null;
        CustomWebClientConfig.CommonHttpClientConfig commonHttpClientConfig = getCommonHttpClientConfig(uriRequest.getServerName(), config);
        CloseableHttpClient httpClient = getHttpClientWithPool(internalRequest.getUrl(), commonHttpClientConfig);
        try {
            response = httpClient.execute(uriRequest);
            HttpEntity responseEntity = response.getEntity();
            int currentStatusCode = response.getStatusLine().getStatusCode();
            result.setStatusCode(currentStatusCode);
            result.setResult(EntityUtils.toString(responseEntity, StandardCharsets.UTF_8));
        } catch (IOException e) {
            result.setStatusCode(0);
            result.addException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    result.addException(e);
                }
            }
            // 非连接池请求调用完毕关闭连接
            if (TrueFalseEnum.isFalse(commonHttpClientConfig.getUsePool())) {
                try {
                    if (httpClient != null) {
                        httpClient.close();
                    }
                } catch (IOException e) {
                    result.addException(e);
                }
            }
        }
        return result;
    }

    private static CloseableHttpClient getHttpClientWithPool(String url, CustomWebClientConfig.CommonHttpClientConfig commonHttpClientConfig) {
        RequestConfig requestConfig = getRequestConfig(url, commonHttpClientConfig);
        log.info("init http client config:{}", requestConfig);
        int retryTimes = TrueFalseEnum.isTrue(commonHttpClientConfig.getRetryFlag()) ? commonHttpClientConfig.getRetryTimes() : 0;
        HttpRequestRetryHandler retryHandler = getHttpRequestRetryHandler(retryTimes);
        HttpClientBuilder builder = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(retryHandler)
                // http client 底层默认开启了连接池复用机制，同时也有默认的保活策略：来着服务端配置的keep_alive，解析响应的timeout即可
                // 因此如果不加以定制，而依靠服务端的设置，一般来说服务端都是2小时，对于客户端来说无法及时识别到 使用了已经失效的链接，进而出现 Connection reset by peer错误
                // 设置建议根据服务活跃程度适度增大或缩小
                .setKeepAliveStrategy(((response, context) -> TimeUnit.MINUTES.toMillis(1)));
        if (TrueFalseEnum.isTrue(commonHttpClientConfig.getUsePool())) {
            builder.setConnectionManager(connManager);
        }
        return builder.build();
    }

    private static CustomWebClientConfig.CommonHttpClientConfig getCommonHttpClientConfig(String server, CustomWebClientConfig config) {
        // 1.优先解析自定义配置
        //  1.1 服务名是否存在， 存在看特殊url是否有自定义，如果没有取服务通用配置
        //  1.2 服务名不存在走兜底
        // 2.兜底选择全局配置
        Map<String, CustomWebClientConfig.CommonHttpClientConfig> customConfig = config.getConfig();
        CustomWebClientConfig.CommonHttpClientConfig commonHttpClientConfig;
        if (customConfig.containsKey(server)) {
            commonHttpClientConfig = customConfig.get(server);
        } else {
            commonHttpClientConfig = config.getGlobal();
        }
        Assert.notNull(commonHttpClientConfig, "http client config loss, please add global or custom config for key 'custom.httpclient'");
        return commonHttpClientConfig;
    }

    private static RequestConfig getRequestConfig(String url, CustomWebClientConfig.CommonHttpClientConfig commonHttpClientConfig) {
        RequestConfig serverConfig = RequestConfig.custom()
                .setConnectTimeout(commonHttpClientConfig.getConnectionTimeOut())
                .setSocketTimeout(commonHttpClientConfig.getSocketTimeOut())
                .setConnectionRequestTimeout(commonHttpClientConfig.getRequestConnectionTimeOut())
                .build();

        if (ObjectUtils.isEmpty(commonHttpClientConfig.getSpecial())) {
            return serverConfig;
        }
        List<CustomWebClientConfig.SpecialUrlConfig> special = commonHttpClientConfig.getSpecial();
        for (CustomWebClientConfig.SpecialUrlConfig specialUrlConfig : special) {
            if (specialUrlConfig.getUrl().contains(url)) {
                serverConfig = RequestConfig.custom()
                        .setConnectTimeout(specialUrlConfig.getConnectionTimeOut())
                        .setSocketTimeout(specialUrlConfig.getSocketTimeOut())
                        .setConnectionRequestTimeout(specialUrlConfig.getRequestConnectionTimeOut())
                        .build();
            }
        }
        return serverConfig;
    }

    private static HttpRequestRetryHandler getHttpRequestRetryHandler(int retryCount) {
        return (exception, executionCount, context) -> {
            if (executionCount > retryCount) {
                return false;
            } else {
                return exception instanceof ConnectTimeoutException || exception instanceof SocketTimeoutException;
            }
        };
    }
}
