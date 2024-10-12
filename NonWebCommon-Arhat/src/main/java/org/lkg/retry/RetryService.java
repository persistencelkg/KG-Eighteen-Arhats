package org.lkg.retry;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lkg.simple.ObjectUtil;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Description: 通用重试框架
 * Author: 李开广
 * Date: 2024/5/15 2:17 PM
 */
@Slf4j
@AllArgsConstructor
public abstract class RetryService {

    private BulkAsyncRetryAble retryAble;

    /**
     * @param throwableFunction
     * @param whetherExceptionContinue 有啥用户不不希望操作产生的异常持续抛出而中断他们的业务逻辑，所以有的时候会进行捕获，然后强制让返回结果为null，
     *                                 最后自己通过对null的判断去做业务判断，这个时候就需要告诉重试框架，
     *                                 true：代表用户手动了捕获了仍需要重试，否则结果的null也是
     * @param <T>
     * @return
     */
    @Nullable
    protected <T> T retryResult(Supplier<T> throwableFunction, boolean whetherExceptionContinue) {
        return doRetry(throwableFunction, 0, (res) -> whetherExceptionContinue);
    }

    protected <T> T retryResult(Supplier<T> throwableFunction) {
        return retryResult(throwableFunction, false);
    }

    // 异步返回结果是没有意义的 可能主线程都结束了
    @Nullable
    protected void retryAsync(Consumer<?> consumer, Supplier<Boolean> res)  {
        doRetry(() -> {
            consumer.accept(null);
            // 强行给一个假的返回值，实际不会使用，目的复用底层能力，由于异步的特点无法直接拿到返回值，这里头提供res方式去
            // 主动获取异步监听结果，因此这里需要开发者在异步回调里定义是否需要重试
            return null;
        }, 0, (r) -> res.get());
    }

    /**
     * 大多框架本身带有异步重试能力，例如RestHighLevelClient，底层基于ApacheHttpClient
     *
     * @param throwableFunction
     * @param count
     */
    private <T> T doRetry(Supplier<T> throwableFunction, int count, Function<T, Boolean> res) {
        T t = null;
        if (count >= retryAble.count()) {
            log.error("[retry service] retry count has surpass the limit {} times, reject execute", retryAble.count());
            return t;
        }
        if (!retryAble.enable()) {
            log(count, "retry stop by hand", null);
            return t;
        }
        try {
            t = throwableFunction.get();

            // 根据用户告诉的结果是否需要重试
            if ((Objects.isNull(t) && Objects.equals(res.apply(null), Boolean.TRUE))) {
                log.warn("current {}th req not match expect, ready for retry", count + 1);
                t = doRetry(throwableFunction, ++count, res);
            }
        } catch (Throwable e) {
            Class<? extends Throwable>[] include = retryAble.include();
            if (ObjectUtil.isEmpty(include) || Arrays.stream(include).noneMatch(ref -> ref.isAssignableFrom(e.getCause().getClass()))) {
                throw e;
            }
            log(count, e.getMessage(), e);
            if (retryAble.interval() > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(retryAble.interval());
                } catch (InterruptedException ignored) {
                }
            }
            // 失败重试
            t = doRetry(throwableFunction, ++count, res);
        }
        return t;

    }

    private static void log(int count, String msg, Throwable e) {
        log.warn("[retry service]: current retry count:{}, happen err:{} ready for retry", count + 1, msg, e);
    }


}
