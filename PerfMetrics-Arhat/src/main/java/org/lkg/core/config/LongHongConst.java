package org.lkg.core.config;

import java.time.Duration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/8 3:09 PM
 */
public interface LongHongConst {

    String KEY_PREFIX = "long-heng.monitor";

    String ENABLE_KEY = "long-heng.monitor.enable";

    String INTERVAL_KEY = "long-heng.monitor.interval";


    Duration DEFAULT_INTERVAL = Duration.ofMinutes(1);
}
