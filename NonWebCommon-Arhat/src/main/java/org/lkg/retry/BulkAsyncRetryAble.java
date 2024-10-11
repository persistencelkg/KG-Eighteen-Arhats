package org.lkg.retry;

import org.lkg.enums.TrueFalseEnum;
import org.lkg.simple.ObjectUtil;

/**
 * 从业务上讲，同步重试大多没有意义，因为你的上游对你的超时时间可能就1-2s 你在同步逻辑重试上游都超时，还有啥意义
 * Description:
 * Author: 李开广
 * Date: 2024/10/10 3:15 PM
 */
public interface BulkAsyncRetryAble extends RetryAble {


    /**
     * 是否开启重试？
     * @return 默认false
     */
    default boolean enable() {
        String s = get(prefix() + ".enable");
        return ObjectUtil.isNotEmpty(s) && TrueFalseEnum.isTrue(Integer.valueOf(s));
    }

    /**
     * 重试间隔,
     *
     * @return 默认 0ms
     */
    default int interval() {
        String s = get(prefix() + ".interval");
        return ObjectUtil.isEmpty(s) ? 0 : Integer.parseInt(s);
    }


    /**
     * 重试次数
     *
     * @return 默认 2次
     */
    default int count() {
        String s = get(prefix() + ".count");
        return ObjectUtil.isEmpty(s) ? 2 : Integer.parseInt(s);
    }

    /**
     * 针对批量操作的大小限制
     *
     * @return 默认500
     */
    default int batchSize() {
        String s = get(prefix() + ".batch-size");
        return ObjectUtil.isEmpty(s) ? 500 : Integer.parseInt(s);
    }
}

