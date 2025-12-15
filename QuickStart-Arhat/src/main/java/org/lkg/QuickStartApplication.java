package org.lkg;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/27 4:52 PM
 */
@EnableAspectJAutoProxy
@SpringBootApplication
//@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.lkg")
@MapperScan("org.lkg")

@Slf4j
public class QuickStartApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(QuickStartApplication.class);
        springApplication.run(args);
    }

    @Bean
    public SmartInitializingSingleton iniit() {
        return  new SmartInitializingSingleton() {
            @Override
            public void afterSingletonsInstantiated() {
                log.info("test------------------------");
                log.debug("debug---------------");
            }
        };
    }
}
