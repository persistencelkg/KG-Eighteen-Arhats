package org.lkg.exception.core;

import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.lkg.exception.BaseException;
import org.lkg.exception.BizException;
import org.lkg.exception.enums.BizExceptionEnum;
import org.lkg.exception.enums.ParamValidExceptionEnum;
import org.lkg.request.CommonIntResp;
import org.lkg.request.CommonReq;
import org.lkg.request.CommonResp;
import org.lkg.request.DefaultResp;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/14 8:32 PM
 */
@RestController
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 用户抛出的异常，无需系统关注
    @ExceptionHandler(value = {
            BizException.class,
            BaseException.class})
    public DefaultResp dealWithBizException(BaseException e) {
        log.warn(e.getMessage(), e);
        return DefaultResp.fail(e.getResponseEnum());
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
            log.warn(e.getMessage(), e);
            return DefaultResp.fail(ParamValidExceptionEnum.VALID_ERROR);
        }
        final List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder builder = new StringBuilder();
        for (FieldError error : fieldErrors) {
            builder.append(error.getDefaultMessage()).append("\n");
        }
        log.warn(builder.toString(), e);
        return DefaultResp.fail(ParamValidExceptionEnum.VALID_ERROR.getCode(), builder.toString());
    }

    // ------------------------------- 以下异常都应该强提醒 --------------------------------------


    // 非受检查的系统异常 五常：ArrayIndexOutOfBoundsException、NullPointerException、ClassCastException、ArithmeticException、IllegalArgumentException
    // 受检异常：网络异常、error类

    @ExceptionHandler(value = {
            SocketTimeoutException.class,
            ConnectException.class,
            InterruptedIOException.class,
    })
    public DefaultResp dealWithTimeoutException(InterruptedIOException e) {
        log.error(e.getMessage(), e);
        return DefaultResp.fail(BizExceptionEnum.TIME_OUT_ERROR);
    }

    @ExceptionHandler(value = {
            ArrayIndexOutOfBoundsException.class,
            NullPointerException.class,
            ClassCastException.class,
            ArithmeticException.class,
            IllegalArgumentException.class
    })
    public DefaultResp defaultUnCheckException(Exception e) {
        // 开发者需要关注
        Metrics.counter("unchecked", "ex", e.getClass().getSimpleName()).increment();
        log.error("please review code: {}", e.getMessage(), e);
        return DefaultResp.fail(BizExceptionEnum.UNCHECKED_ERROR);
    }


    @ExceptionHandler(value = {Throwable.class})
    public CommonIntResp<Object> dealWithBottomException(Throwable e) {
        // 开发者需要关注
        Metrics.counter("unknown").increment();
        log.error(e.getMessage(), e);
        return CommonIntResp.fail(BizExceptionEnum.UNKNOWN_ERROR);
    }


}
