package org.lkg.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:52 AM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonIntResp<T> extends CommonResp<T, java.lang.Integer> {
    private static final long serialVersionUID = 1L;
    private T data;
    private Integer code;
    private String message;
}
