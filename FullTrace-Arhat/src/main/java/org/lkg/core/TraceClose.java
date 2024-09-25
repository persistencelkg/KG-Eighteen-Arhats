package org.lkg.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Closeable;
import java.io.IOException;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/24 2:03 PM
 */
@Data
@AllArgsConstructor
public class TraceClose implements Closeable {

    private final Trace trace;
    private final TraceScope traceScope;


    @Override
    public void close() throws IOException {
        traceScope.close();
    }
}
