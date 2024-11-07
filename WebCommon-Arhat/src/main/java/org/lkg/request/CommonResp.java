package org.lkg.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.lkg.exception.IResponseEnum;

import java.io.Serializable;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:52 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class CommonResp<D, Code> implements Serializable {
    private static final long serialVersionUID = 1L;
    private D data;
    private Code code;
    private String message;

    public static final Integer COMMON_SUC = 200;
    public static final Integer COMMON_FAIL = -1;


    protected CommonResp(D data, Code code) {
        this.data = data;
        this.code = code;
    }



    // --  int  code ------
    public static <T> CommonIntResp<T> successInt(T data) {
        return new CommonIntResp<>(data, CommonResp.COMMON_SUC);
    }

    public static <T> CommonIntResp<T> successInt(T data, Integer c) {
        return new CommonIntResp<>(data, c);
    }

    public static <T> CommonIntResp<T> failInt(Integer c, String message) {
        return new CommonIntResp<>(null, c, message);
    }

    public static <T> CommonIntResp<T> failInt(String message) {
        return new CommonIntResp<>(null, CommonResp.COMMON_FAIL, message);
    }
    // --  string code ------

    public static DefaultResp success(Object data) {
        return new DefaultResp(data, COMMON_SUC.toString());
    }

    public static DefaultResp success(Object data, String code) {
        return new DefaultResp(data, code);
    }


    public static DefaultResp fail(String c, String message) {
        return new DefaultResp(null, c, message);
    }
    public static DefaultResp fail( String message) {
        return new DefaultResp(null, COMMON_FAIL.toString(), message);
    }

    public static DefaultResp fail(IResponseEnum iResponseEnum) {
        return new DefaultResp(null, iResponseEnum.getCode(), iResponseEnum.getMessage());
    }
}
