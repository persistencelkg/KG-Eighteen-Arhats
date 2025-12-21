package org.lkg.exception.core;

import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.lkg.exception.CommonException;
import org.lkg.exception.enums.CommonExceptionEnum;
import org.lkg.request.CommonResp;
import org.lkg.request.DefaultResp;
import org.lkg.utils.KgLogUtil;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/14 8:32 PM
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 用户抛出的异常，无需系统关注
    @ExceptionHandler(value = {
           CommonException.class})
    public DefaultResp dealWithBizException(CommonException e) {
        KgLogUtil.printBizError(e.getMessage(), e);
        return DefaultResp.fail(e.getIErrorCode());
    }

    // 参数异常
    @ExceptionHandler(value = {

            BindException.class,
            MethodArgumentNotValidException.class,
            // 其他类也是因为参数格式引起的统一处理
            MethodArgumentTypeMismatchException.class})
    public DefaultResp dealWithArgumentException(Exception e) {
        BindingResult result;
        if (e instanceof BindException) {
            result = ((BindException) e).getBindingResult();
        } else if (e instanceof  MethodArgumentNotValidException) {
            result = ((MethodArgumentNotValidException) e).getBindingResult();
        } else {
            return DefaultResp.fail(e.getMessage());
        }
        final List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder builder = new StringBuilder();
        for (FieldError error : fieldErrors) {
            builder.append(error.getDefaultMessage()).append("\n");
        }
        KgLogUtil.printBizError(builder.toString(), e);
        return DefaultResp.fail(e.getMessage(), builder.toString());
    }

    // ------------------------------- 以下异常都应该强提醒 --------------------------------------


    // 非受检查的系统异常 五常：ArrayIndexOutOfBoundsException、NullPointerException、ClassCastException、ArithmeticException、IllegalArgumentException
    // 受检异常：网络异常、error类

    @ExceptionHandler(value = {
            SocketTimeoutException.class
    })
    public DefaultResp dealWithTimeoutException(SocketTimeoutException e) {
        KgLogUtil.printSalError(e.getMessage(), e);
        return DefaultResp.fail(CommonExceptionEnum.TIME_OUT_SAL_ERROR);
    }


    @ExceptionHandler(value = {Throwable.class})
    public DefaultResp dealWithBottomException(Throwable e) {
        // 开发者需要关注
        Metrics.counter("unknown").increment();
        KgLogUtil.printSysError(e.getMessage(), e);
        return CommonResp.fail(CommonExceptionEnum.UNKNOWN_SYS_ERROR);
    }
}
