package org.lkg.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:52 AM
 */
@NoArgsConstructor
public class CommonIntResp<T> extends CommonResp<T, Integer> {
    private static final long serialVersionUID = 1L;

    public CommonIntResp(T t, Integer integer, String message) {
        super(t, integer, message);
    }

    public CommonIntResp(T t, Integer integer) {
        super(t, integer);
    }
}
