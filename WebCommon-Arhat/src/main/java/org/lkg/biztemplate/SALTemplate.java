package org.lkg.biztemplate;

import org.lkg.exception.enums.CommonExceptionEnum;
import org.lkg.exception.enums.MonitorStatus;
import org.lkg.exception.enums.MonitorType;
import org.lkg.utils.KgLogUtil;
import org.lkg.utils.JacksonUtil;

import java.util.Objects;
import java.util.function.Function;

/**
 * @date: 2025/12/13 12:01
 * @author: li kaiguang
 */
public class SALTemplate {


    public static <T, R>  R exec(String method, T request, Function<T, R> function) {
        String requestStr = JacksonUtil.writeValue(request);
        long start = System.currentTimeMillis();
        MonitorStatus ret = MonitorStatus.FAIL;
        IErrorCode iErrorCode  = null;
        R resp = null;
        try {

            resp = function.apply(request);

            ret = MonitorStatus.SUCCESS;
            iErrorCode = CommonExceptionEnum.SAL_EXEC_SUCCESS;

            return resp;
        }  catch (CommonException commonException) {
            KgLogUtil.printBizError("{} request:{} biz error:{}", method, requestStr, commonException.getMessage(), commonException);
            iErrorCode = commonException.getIErrorCode();
            throw  commonException;
        } catch (Throwable th) {
            KgLogUtil.printSysError("{} request:{} sys fail, th:", method, requestStr, th);
            iErrorCode = CommonExceptionEnum.UNKNOWN_SAL_EXEC_ERROR;
            throw CommonException.fail(iErrorCode);
        } finally {
            KgLogUtil.monitor(MonitorType.SAL_HTTP,  ret, iErrorCode, "SalTemplate.exec", start);
            if (Objects.equals(ret, MonitorStatus.SUCCESS)) {
                // 序列化策略和内存对象分离
                KgLogUtil.printSalInfo("{} exec success request:%s, resp:%s, %sms", method, requestStr, JacksonUtil.writeValue(resp), start);
            } else {
                KgLogUtil.printSalError("{} exec fail request:%s, resp:%s, %sms", method, requestStr, iErrorCode.getMessage(), start);
            }
        }
    }


}
