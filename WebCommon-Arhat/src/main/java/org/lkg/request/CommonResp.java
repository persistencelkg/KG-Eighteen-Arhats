package org.lkg.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.lkg.exception.IResponseEnum;

import java.io.Serializable;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:52 AM
 */
@Data
@AllArgsConstructor
public class CommonResp<D, Code> implements Serializable {
    private static final long serialVersionUID = 1L;
    private D data;
    private Code code;
    private String message;

    public CommonResp(D data, Code code) {
        this.data = data;
        this.code = code;
    }



    public static <T> CommonIntResp<T> success(T data, String c) {
        return new CommonIntResp<>(data, c);
    }

    public static <T> CommonIntResp<T> fail(String c, String message) {
        return new CommonIntResp<>(null, c, message);
    }

    public static <T> CommonIntResp<T> fail(IResponseEnum iResponseEnum) {
        return new CommonIntResp<>(null, iResponseEnum.getCode(), iResponseEnum.getMessage());
    }
}
