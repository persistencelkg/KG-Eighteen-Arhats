package org.lkg.core.config;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/8 3:09 PM
 */
public interface LongHongConst {

    // base config
    String KEY_PREFIX = "long-heng.monitor";

    String ENABLE_KEY = "long-heng.monitor.enable";

    String INTERVAL_KEY = "long-heng.monitor.interval";

    String ALARM_WINDOW_SIZE_KEY = KEY_PREFIX + ".window.size";

    Integer MAX_NAMESPACE_COUNT = 10000;

    // exporter config
    String KAFKA_CONFIG_KEY = "long-heng.kafka.config";

    String KAFKA_CONFIG_BATCH_SIZE = KAFKA_CONFIG_KEY + ".poll-size";

    // meter config
    String DISABLE_METER_KEY = "long-heng.disable.namespace";

    Integer ALARM_WINDOW_SIZE = 10000;

    Duration DEFAULT_INTERVAL = Duration.ofMinutes(1);

    interface TagConst {
        String DEPT = "dept";
        String SERVER_NAME = "app.name";
        String IP = "server.ip";
        String ENV = "env";

        Set<String> INTERNAL_TAG = new HashSet<String>(){{
            add(DEPT);
            add(SERVER_NAME);
            add(IP);
            add(ENV);
        }};
    }
}
