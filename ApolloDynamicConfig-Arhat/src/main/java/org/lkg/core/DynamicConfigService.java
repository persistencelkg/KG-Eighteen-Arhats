package org.lkg.core;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/20 7:29 PM
 */
public interface DynamicConfigService {

    String getStrValue(String key, String def);

    String getEnv();

    void addChangeKeyPostHandler(String key, KeyChangeHandler keyChangeHandler);
}
