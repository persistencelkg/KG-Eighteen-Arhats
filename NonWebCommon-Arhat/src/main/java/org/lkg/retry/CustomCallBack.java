package org.lkg.retry;

import org.lkg.function.CheckedConsumer;

import java.util.function.Consumer;

/**
 * Description:自定义回调
 * Author: 李开广
 * Date: 2024/5/15 11:38 AM
 */
public interface CustomCallBack<Res, E extends Exception> {

    void stop(boolean flag);

    void onSuccess(Res res);

    void onFail(E e);

    static <Res> CustomCallBack<Res, ? extends Exception> wrap(CheckedConsumer<Res, ? extends Exception> checkedConsumer, Consumer<Exception> consumer) {
        return new CustomCallBack<Res, Exception>() {
            @Override
            public void stop(boolean flag) {

            }

            @Override
            public void onSuccess(Res res) {
                try {
                    checkedConsumer.check(res);
                } catch (Exception e) {
                    onFail(e);
                }
            }

            @Override
            public void onFail(Exception e) {
                consumer.accept(e);
            }
        };
    }

    static <Res> CustomCallBack<Res, ? extends Exception> wrap(Runnable run) {
        return wrap(ref -> run.run(), ref -> run.run());
    }
}
