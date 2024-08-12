package org.lkg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/12 8:35 PM
 */
@Data
@Component
@ConfigurationProperties("influx")
public class InfluxDbConfig {

    private String url;

    private String userName;

    private String password;

    private String database;
}
