package org.lkg.request;

import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/14 9:05 PM
 */
@NoArgsConstructor
public class DefaultResp extends CommonResp<Object, String> {

    public DefaultResp(Object data, String code, String message) {
        super(data, code, message);
    }

    public DefaultResp(Object data, String code) {
        super(data, code);
    }
}
