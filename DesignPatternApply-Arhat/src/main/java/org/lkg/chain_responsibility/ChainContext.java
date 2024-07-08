package org.lkg.chain_responsibility;

import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 10:41 PM
 */
public class ChainContext {

    private static final ThreadLocal<Object> CONTEXT = ThreadLocal.withInitial(Object::new);


    public static void setContext(Object context) {
        if (Objects.isNull(context)) {
            CONTEXT.remove();
        } else {
            CONTEXT.set(context);
        }
    }

    public static Object getContext() {
        return CONTEXT.get();
    }
}
