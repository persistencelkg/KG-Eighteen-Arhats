package org.lkg.request;

import lombok.Data;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:52 AM
 */
@Data
public class CommonResp<T,C> {

    private T data;
    private C code;
    private String message;
}
