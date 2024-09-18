package org.lkg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
//@MapperScan("org.lkg")
public class QuickStartApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(QuickStartApplication.class);
        springApplication.run(args);
    }
}
