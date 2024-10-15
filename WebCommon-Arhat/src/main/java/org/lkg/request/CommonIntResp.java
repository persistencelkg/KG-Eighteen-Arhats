package org.lkg.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:52 AM
 */

public class CommonIntResp<T> extends CommonResp<T, String> {
    private static final long serialVersionUID = 1L;

    public CommonIntResp(T t, String integer, String message) {
        super(t, integer, message);
    }

    public CommonIntResp(T t, String integer) {
        super(t, integer);
    }
}
