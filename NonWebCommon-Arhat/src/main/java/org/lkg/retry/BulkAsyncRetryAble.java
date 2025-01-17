package org.lkg.retry;

import org.lkg.enums.TrueFalseEnum;
import org.lkg.exception.RetryException;
import org.lkg.utils.ObjectUtil;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * 从业务上讲，同步重试大多没有意义，因为你的上游对你的超时时间可能就1-2s 你在同步逻辑重试上游都超时，还有啥意义
 * Description:
 * Author: 李开广
 * Date: 2024/10/10 3:15 PM
 */
public interface BulkAsyncRetryAble extends RetryAble {

    int maxRetryInterval = 1500;

    int retryCount = 2;

    int batchSize = 500;

    default Class<? extends Throwable>[] include(){
        return new Class[]{
                SocketTimeoutException.class,
                ConnectException.class,
                InterruptedIOException.class,
                RetryException.class
        };
    }

    /**
     * 是否开启重试？
     * @return 默认false
     */
    default boolean enable() {
        String s = get(prefix() + ".enable");
        return ObjectUtil.isNotEmpty(s) && TrueFalseEnum.isTrue(Integer.valueOf(s));
    }

    /**
     * 重试间隔
     *
     * @return 默认 0ms
     */
    default int interval() {
        String s = get(prefix() + ".interval");
        return ObjectUtil.isEmpty(s) ? 0 : Math.min(Integer.parseInt(s), maxInterval());
    }

    /**
     * 最大重试间隔，方式因为用户配置较大的重试时间，导致线程被持续阻塞，如果这样的线程很多，就会非常影响性能
     * @return 如果手动修改了上限而引发的问题需要由开发者自行承担
     */
    default int maxInterval() {
        String s = get(prefix() + ".max-interval");
        return ObjectUtil.isEmpty(s) ? maxRetryInterval : Integer.parseInt(s);
    }


    /**
     * 重试次数
     *
     * @return 默认 2次
     */
    default int count() {
        String s = get(prefix() + ".count");
        return ObjectUtil.isEmpty(s) ? retryCount : Integer.parseInt(s);
    }

    /**
     * 针对批量操作的大小限制
     *
     * @return 默认500
     */
    default int batchSize() {
        String s = get(prefix() + ".batch-size");
        return ObjectUtil.isEmpty(s) ? batchSize : Integer.parseInt(s);
    }
}

