package org.lkg.request;

import org.lkg.exception.IResponseEnum;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/14 9:05 PM
 */
public class DefaultResp extends CommonIntResp<Object>{

    public DefaultResp(Object o, String code, String message) {
        super(o, code, message);
    }

    public DefaultResp(Object o, String code) {
        super(o, code);
    }

    public static DefaultResp fail(String c, String message) {
        return new DefaultResp(null, c, message);
    }

    public static DefaultResp fail(IResponseEnum iResponseEnum) {
        return new DefaultResp(null, iResponseEnum.getCode(), iResponseEnum.getMessage());
    }
}
