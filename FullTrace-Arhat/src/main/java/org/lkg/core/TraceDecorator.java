package org.lkg.core;

import java.util.function.Supplier;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/24 1:46 PM
 */
public interface TraceDecorator {

    TraceScope decorator(Trace trace, TraceScope traceScope);
}
