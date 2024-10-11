package org.lkg.retry;

import javafx.util.Callback;

/**
 * Description: 具备可重试能力接口
 * Author: 李开广
 * Date: 2024/5/15 11:25 AM
 */
public interface RetryAble {

    String prefix();


    String get(String key);

    // 阶梯重试

}
