package org.lkg.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:52 AM
 */
@Data
public class CommonResp<T, C> implements Serializable {
    private static final long serialVersionUID = 1L;
    private T data;
    private C code;
    private String message;
}
