package org.lkg.core.exception;

import lombok.AllArgsConstructor;
import org.lkg.core.Trace;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/17 3:12 PM
 */
public class TraceTimeoutException extends RuntimeException{

    public TraceTimeoutException(String err) {
        super(err);
    }
}
