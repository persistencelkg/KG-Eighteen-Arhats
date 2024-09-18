package org.lkg.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/12 8:35 PM
 */
@Data
@Component
@ConfigurationProperties("influx")
@ConditionalOnProperty(value = "influx.enable", havingValue = "1", matchIfMissing = true)
public class InfluxDbConfig {


    private Map<String, DbConfig> config;

    @Data
    public  static class DbConfig {

        private String url;

        private String userName;

        private String password;

        private String database;
    }


}
