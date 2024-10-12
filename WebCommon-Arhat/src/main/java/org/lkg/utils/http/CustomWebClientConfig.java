package org.lkg.utils.http;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lkg.enums.TrueFalseEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 自定义的全局配置类
 * Author: 李开广
 * Date: 2024/7/1 11:15 AM
 */
@Data
@Component
@ConfigurationProperties(prefix = "custom.httpclient")
@Slf4j
public class CustomWebClientConfig {

    /**
     * 全局最大连接数： 因为http client 是共享连接池，可能请求多个三方服务，
     * 使用时需要仔细评估这些三方服务的连接数瓶颈，切记不可太保守
     * 一下配置代表可以支持10个业务 每个业务最大连接数100，超过只能等待
     */
    private int maxTotal = 1000;

    /**
     * 全局配置
     */
    private CommonHttpClientConfig global;

    /**
     * 自定义配置： key 服务名 value 服务下的对应接口的配置
     */
    private Map<String, CommonHttpClientConfig> config;

    @Data
    public static class CommonHttpClientConfig {

        private String[] urlList;


        /**
         * 对特定主机、url的最大连接数，可以理解请求某一个三方的最大连接数，
         * 最好取所有三方连接数的最大值
         */
        private int maxPerRoute = 100;

        /**
         * 默认连接超时 3s
         */
        private int connectionTimeOut = 2999;

        /**
         * 默认读超时 1s
         */
        private int socketTimeOut = 999;

        private int requestConnectionTimeOut = 1000;

        /**
         * 是否需要重试，默认不重试
         */
        private int retryFlag = TrueFalseEnum.FALSE.getCode();

        /**
         * 默认重试次数
         */
        private int retryTimes = 2;

        /**
         * 是否使用连接池
         */
        private int usePool = TrueFalseEnum.TRUE.getCode();

        private List<SpecialUrlConfig> special;
    }

    @Data
    public static class SpecialUrlConfig {
        /**
         * 通用接口的定义
         */
        private String url;

        private int connectionTimeOut = 2999;
        /**
         * 默认读超时 1s
         */
        private int socketTimeOut = 999;

        private int requestConnectionTimeOut = 1000;

        /**
         * 是否需要重试，默认不重试
         */
        private int retryFlag = TrueFalseEnum.FALSE.getCode();

        /**
         * 默认重试次数
         */
        private int retryTimes = 3;

        private int usePool = TrueFalseEnum.TRUE.getCode();
    }



    /**
     * 根据用户配置的局部连接数，和实际已经分配后剩余的连接数进行比较，
     * 如果新配置的局部连接数 < 剩余可用连接  配置合理
     * 如果 >= 提示，maxTotal可能存在性能问题，不足矣完成
     */
    public void checkPerRouteEnough() {

    }



}
