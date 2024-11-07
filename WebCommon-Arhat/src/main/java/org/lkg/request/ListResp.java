

package org.lkg.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lkg.exception.IResponseEnum;

import java.io.Serializable;
import java.util.List;

import static org.lkg.request.CommonResp.COMMON_FAIL;
import static org.lkg.request.CommonResp.COMMON_SUC;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:52 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListResp<D, Code> implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<D> data;
    private Code code;
    private String message;

    protected ListResp(List<D> data, Code code) {
        this.data = data;
        this.code = code;
    }

    public static <D> ListResp<D, String> success(List<D> data) {
        return new ListResp<>(data, COMMON_SUC.toString());
    }

    public static <D> ListResp<D, String> success(List<D> data, String code) {
        return new ListResp<>(data, code);
    }

    public static <D> ListResp<D, String> fail(String msg) {
        return new ListResp<>(null, COMMON_FAIL.toString(), msg);
    }

    public static <D> ListResp<D, String> fail(String code, String msg) {
        return new ListResp<>(null, code, msg);
    }

    public static <D> ListResp<D, Integer> successInt(List<D> data) {
        return new ListResp<>(data, COMMON_SUC);
    }

    public static <D> ListResp<D, Integer> successInt(List<D> data, int code) {
        return new ListResp<>(data, code);
    }

    public static <D> ListResp<D, Integer> failInt(String msg) {
        return new ListResp<>(null, COMMON_FAIL, msg);
    }

    public static <D> ListResp<D, Integer> failInt(int code, String msg) {
        return new ListResp<>(null, code, msg);
    }


}
