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


    private final static InheritableThreadLocal<Trace> inheritableThreadLocal = new InheritableThreadLocal() {
        @Override
        protected Trace initialValue() {
            return new Trace();
        }
    };


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
        try (Trace trace = inheritableThreadLocal.get()) {
            inheritableThreadLocal.remove();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws IOException {

    }
}
