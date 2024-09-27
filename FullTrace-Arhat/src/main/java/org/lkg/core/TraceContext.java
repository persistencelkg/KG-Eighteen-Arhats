package org.lkg.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/23 9:12 PM
 */
public class TraceContext implements Closeable {


    private final static ThreadLocal<Trace> inheritableThreadLocal = InheritableThreadLocal.withInitial(() -> new Trace());


    public static Trace getCurrentContext() {
        return inheritableThreadLocal.get();
    }

    public static void setContext(Trace trace) {
        if (Objects.isNull(trace)) {
            remove();
        } else {
            inheritableThreadLocal.set(trace);
        }
    }

    public static void remove() {
        inheritableThreadLocal.remove();
    }


    @Override
    public void close() throws IOException {

    }
}
