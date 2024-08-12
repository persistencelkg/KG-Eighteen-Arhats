package org.lkg.core.config;

import lombok.Data;
import org.lkg.core.DynamicConfigManger;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 9:30 PM
 */
@Data
// TODO 基于注解的配置
public class LongHongMonitorConfig {

    private String dept;

    private String serverName;

    private String env;

    private String secret;

    private String url;

    private Duty duty;

    private List<AlarmRule> alarmRule;

    @Data
    public static class Duty {
        private String cron;
        private String phone;
    }


    @Data
    public static class AlarmRule {
        private String namespace;
        // max > 3000 -> spring expression
        private String expression;

        private boolean phoneAlarm;
    }

    public static LongHongMonitorConfig getInstance() {
        return DynamicConfigManger.getConfigValue(LongHongMonitorConfig.class);
    }
}
