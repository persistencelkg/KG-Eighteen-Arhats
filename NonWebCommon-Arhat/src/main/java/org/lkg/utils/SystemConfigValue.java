package org.lkg.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: 李开广
 * Date: 2023/10/12 3:09 PM
 */
@Component
@Data
public class SystemConfigValue {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.profiles.active}")
    private String env;


}
