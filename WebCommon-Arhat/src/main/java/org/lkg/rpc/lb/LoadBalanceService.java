package org.lkg.rpc.lb;

import lombok.extern.slf4j.Slf4j;
import org.lkg.exception.CommonException;
import org.lkg.exception.enums.CommonExceptionEnum;
import org.lkg.retry.RetryService;
import org.lkg.utils.JacksonUtil;
import org.lkg.utils.ObjectUtil;
import org.lkg.utils.http.CustomWebClientConfig;
import org.lkg.utils.matcher.AntPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/22 4:28 PM
 */
@Service
@Slf4j
public class LoadBalanceService extends RetryService {

    @Autowired(required = false)
    private LoadBalancerClient loadBalancerClient;

    @Autowired(required = false)
    private RestTemplate restTemplate;

    @Resource
    private CustomWebClientConfig okHttpCustomConfig;

    private final static AntPathMatcher antPathMatcher = new AntPathMatcher();

    public String getUrl(String serviceName, String url) {
        if (ObjectUtil.isEmpty(serviceName) || ObjectUtil.isEmpty(url)) {
            throw CommonException.fail(CommonExceptionEnum.PARAM_VALID_ERROR);
        }
        ServiceInstance choose = loadBalancerClient.choose(serviceName);
        if (ObjectUtil.isEmpty(choose)) {
            throw CommonException.fail(CommonExceptionEnum.SERVICE_INVOKE_ERROR);
        }
        return String.format("%s%s", choose.getUri().toString(), url);
    }

    public <T> T post(Object request, ThirdServiceInvokeEnum thirdServiceInvokeEnum) {
        return post(request, thirdServiceInvokeEnum, null);
    }

    public <T> T post(Object request, ThirdServiceInvokeEnum thirdServiceInvokeEnum, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (ObjectUtil.isNotEmpty(headers)) {
            headers.forEach(httpHeaders::add);
        }
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        String param = JacksonUtil.writeValue(request);
        if (ObjectUtil.isEmpty(param)) {
            throw CommonException.fail(CommonExceptionEnum.PARAM_VALID_ERROR);
        }
        // 自定义超时时间
//        customTTL(thirdServiceInvokeEnum);

        String url = getUrl(thirdServiceInvokeEnum.getServiceName(), thirdServiceInvokeEnum.getUrl());
        HttpEntity<String> entity = new HttpEntity<>(param, httpHeaders);
        ResponseEntity<String> response = retryResult(() -> restTemplate.postForEntity(url, entity, String.class));
        return (T) JacksonUtil.readObj(response.getBody(), thirdServiceInvokeEnum.getTypeReference());
    }

    private void customTTL(ThirdServiceInvokeEnum invokeEnum) {
        OkHttp3ClientHttpRequestFactory requestFactory = (OkHttp3ClientHttpRequestFactory) restTemplate.getRequestFactory();
        Map<String, CustomWebClientConfig.CommonHttpClientConfig> config = okHttpCustomConfig.getConfig();
        if (ObjectUtil.isEmpty(config)) {
            return;
        }
        CustomWebClientConfig.CommonHttpClientConfig commonHttpClientConfig = config.get(invokeEnum.getServiceName());
        if (ObjectUtil.isEmpty(commonHttpClientConfig)) {
            return;
        }
        requestFactory.setReadTimeout(commonHttpClientConfig.getSocketTimeOut());
        requestFactory.setConnectTimeout(commonHttpClientConfig.getConnectionTimeOut());
        List<CustomWebClientConfig.SpecialUrlConfig> special = commonHttpClientConfig.getSpecial();
        if (ObjectUtil.isNotEmpty(special)) {
            for (CustomWebClientConfig.SpecialUrlConfig specialUrlConfig : special) {
                if (antPathMatcher.match("/**" + specialUrlConfig.getUrl(), invokeEnum.getUrl())) {
                    requestFactory.setReadTimeout(specialUrlConfig.getSocketTimeOut());
                    requestFactory.setConnectTimeout(specialUrlConfig.getConnectionTimeOut());
                    break;
                }
            }
        }

        restTemplate.setRequestFactory(requestFactory);
    }

}
