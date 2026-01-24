package org.lkg.biztemplate;

import org.lkg.exception.CommonException;
import org.lkg.exception.IErrorCode;
import org.lkg.exception.enums.CommonExceptionEnum;
import org.lkg.exception.enums.MonitorStatus;
import org.lkg.exception.enums.MonitorType;
import org.lkg.utils.JacksonUtil;
import org.lkg.utils.KgLogUtil;
import org.lkg.utils.ObjectUtil;
import org.lkg.utils.ValidateUtil;

import java.util.function.Function;

/**
 * @date: 2025/12/13 12:00
 * @author: li kaiguang
 */
public class InnerExecTemplate {
    public static <T, R>  R exec(String method, T request, Function<T, R> function) {
        String requestStr = JacksonUtil.writeValue(request);
        long start = System.currentTimeMillis();
        MonitorStatus ret = MonitorStatus.FAIL;
        IErrorCode iErrorCode  = null;
        try {
            String errMsg = ValidateUtil.validateRequest(request);
            if (ObjectUtil.isNotEmpty(errMsg)) {
                throw CommonException.fail(CommonExceptionEnum.PARAM_VALID_ERROR, errMsg);
            }

            R resp = function.apply(request);

            ret = MonitorStatus.SUCCESS;
            iErrorCode = CommonExceptionEnum.EXEC_SUCCESS;

            return resp;
        }  catch (CommonException commonException) {
            KgLogUtil.printBizError("{} request:{} biz error:{}", method, requestStr, commonException.getMessage(), commonException);
            iErrorCode = commonException.getIErrorCode();
            throw  commonException;
        } catch (Throwable th) {
            KgLogUtil.printSysError("{} request:{} sys fail, th:", method, requestStr, th);
            iErrorCode = CommonExceptionEnum.UNKNOWN_SYS_ERROR;
            throw CommonException.fail(iErrorCode);
        } finally {
            KgLogUtil.monitor(MonitorType.INNER,  ret, iErrorCode, "InnerExecTemplate.exec", start);
        }
    }



    public String test(Object obj) {
        return exec("test", obj, ref -> {
            return  "";
        });
    }

}
