package org.lkg.core.service;

import org.lkg.core.DynamicConfigManger;
import org.lkg.core.config.LongHongConst;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.lkg.core.config.LongHongConst.ALARM_WINDOW_SIZE;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/16 1:44 PM
 */
public class AlarmWindowManger {

    private String key;
    private int count = 0;
    private long updateTime = System.currentTimeMillis();

    private static Map<String, AlarmWindowManger> POOL = new LinkedHashMap() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() >= DynamicConfigManger.getInt(LongHongConst.ALARM_WINDOW_SIZE_KEY, ALARM_WINDOW_SIZE);
        }
    };

    public static AlarmWindowManger getOrCreate(String key) {
        return POOL.computeIfAbsent(key, AlarmWindowManger::new);
    }

    private AlarmWindowManger(String key) {
        this.key = key;
    }


    public void failCount() {
        long lastUpdate = updateTime;
        updateTime = System.currentTimeMillis();
        if (Duration.ofMillis(updateTime - lastUpdate).toMillis() > DynamicConfigManger.getInt(LongHongConst.INTERVAL_KEY)) {
            count = 1;
        }
        ++ count;
    }

    public Map<String, Object> paramForCountMap(String name) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(name, count);
        return map;
    }

    public static void remove(String key) {
        POOL.remove(key);
    }
}
