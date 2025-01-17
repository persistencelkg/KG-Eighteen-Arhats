package org.lkg.utils.http;

import lombok.extern.slf4j.Slf4j;
import org.lkg.utils.JacksonUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/1 5:12 PM
 */
@Slf4j
@Component
public class CustomWebClientHolder {

    @Resource
    private CustomWebClientConfig customWebClientConfig;

    private static CustomWebClientConfig INSTANCE;

    @PostConstruct
    public void init() {
        Assert.notNull(customWebClientConfig, "自定义web client 配置缺失");
        log.info(JacksonUtil.writeValue(customWebClientConfig));
        INSTANCE = customWebClientConfig;
    }

    public static CustomWebClientConfig getCustomWebClientConfig() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new CustomWebClientConfig();
            INSTANCE.setGlobal(new CustomWebClientConfig.CommonHttpClientConfig());
        }
        return INSTANCE;
    }
}
