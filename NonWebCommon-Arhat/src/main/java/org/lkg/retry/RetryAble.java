package org.lkg.retry;

import javafx.util.Callback;

/**
 * Description: 具备可重试能力接口
 * Author: 李开广
 * Date: 2024/5/15 11:25 AM
 */
public interface RetryAble {

    default int retryCount() {
        return 3;
    }

    default int retrySleepMills() {
        return 0;
    }


    // 阶梯重试


}
