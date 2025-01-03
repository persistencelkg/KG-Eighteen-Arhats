package org.lkg.core;

import java.io.IOException;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/23 9:12 PM
 */
public class TraceContext{


    private final static InheritableThreadLocal<Trace> inheritableThreadLocal = new InheritableThreadLocal<>();


    public static Trace getCurrentContext() {
        return inheritableThreadLocal.get();
    }

    public static void setContextAfterRemove(Trace trace) {
        remove();
        inheritableThreadLocal.set(trace);
    }

    public static void remove() {
        // 确保资源的彻底清理
        try (Trace trace = inheritableThreadLocal.get()) {
            inheritableThreadLocal.remove();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
