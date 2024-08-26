package org.lkg.core;

import com.ctrip.framework.apollo.enums.PropertyChangeType;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/19 9:03 PM
 */
public class KeyChange {

    private String namespace;
    private String key;
    private String oldVal;
    private String newVal;
    // 事件类型
    private PropertyChangeType propertyChangeType;
}
