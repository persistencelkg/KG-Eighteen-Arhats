package org.lkg.exception;

import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 7:34 PM
 */
public interface ExceptionSystemConst {

    // 未知错误，返回值获取不到 可能是超时或者连接中断导致
    int TIMEOUT_MAYBE_ERR_CODE = -1;

    static void assertTemplate(boolean res, String... tempate) {
        String format = MessageFormat.format(tempate[0], Arrays.copyOfRange(tempate, 1, tempate.length));
        Assert.isTrue(res, format);
    }


    public static void main(String[] args) {
        assertTemplate(1 < 0, "lkg:{0}:{1}", "tx", "22");
    }
}
