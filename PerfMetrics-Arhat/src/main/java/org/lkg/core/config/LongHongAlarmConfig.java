package org.lkg.core.config;

import lombok.Data;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.DynamicKeyConfig;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 9:30 PM
 */
@Data
@DynamicKeyConfig(key = LongHongConst.KEY_PREFIX)
public class LongHongAlarmConfig {

    private String dept;

    private String serverName;

    private String env;

    private String secret;

    private String url;

    private Duty duty;

    // key: namespace
    private Map<String, List<AlarmRule>> alarmRule;

    @Data
    public static class Duty {
        private String cron;
        private String phone;
    }


    @Data
    public static class AlarmRule {
        // (count >= 3000) && (p995 > 1)
        private String expression;
        // ding ding msg -> 短信 -> phone
        private boolean phoneAlarm;
    }

    public static LongHongAlarmConfig getInstance() {
        return DynamicConfigManger.getAnnotationConfig(LongHongAlarmConfig.class);
    }
}
