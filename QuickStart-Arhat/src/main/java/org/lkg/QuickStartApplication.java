package org.lkg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/27 4:52 PM
 */
@SpringBootApplication
public class QuickStartApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(QuickStartApplication.class);
        springApplication.run(args);
    }
}
