package org.lkg.rpc.feign;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignCustomConfig {
    /**
     * 全局重试次数
     * @return
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000, 1000, 3);
    }

    /* 无需手动注入client，默认自带注入， details：DefaultFeignLoadBalancedConfiguration */
}
