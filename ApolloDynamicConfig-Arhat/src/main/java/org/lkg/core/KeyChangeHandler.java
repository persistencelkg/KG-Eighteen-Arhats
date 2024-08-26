package org.lkg.core;

import com.ctrip.framework.apollo.model.ConfigChange;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/19 9:03 PM
 */
public interface KeyChangeHandler {

    void onChange(ConfigChange keyChange);
}
