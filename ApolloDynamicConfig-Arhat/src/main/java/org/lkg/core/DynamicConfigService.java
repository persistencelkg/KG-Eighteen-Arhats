package org.lkg.core;

import java.util.Set;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/20 7:29 PM
 */
public interface DynamicConfigService extends KeyConfigService{

    String getEnv();

    void addChangeKeyPostHandler(String key, KeyChangeHandler keyChangeHandler);

    Set<KeyChangeHandler> setAllChangeKeySet(String key);
}
