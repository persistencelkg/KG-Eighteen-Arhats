package org.lkg.log;

import org.lkg.exception.IErrorCode;
import org.lkg.exception.enums.MonitorStatus;
import org.lkg.exception.enums.MonitorType;
import org.lkg.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * @date: 2025/12/13 20:08
 * @author: li kaiguang
 */
public class KgLogUtil {
    private static final Logger BIZ_INFO_LOG = LoggerFactory.getLogger("BIZ_INFO_LOG");
    /**
     * 预期的业务错误，如参数检查，超时异常
     */
    private static final Logger BIZ_ERROR_LOG = LoggerFactory.getLogger("BIZ_ERROR_LOG");
    /**
     * 系统错误，非预期，
     */
    private static final Logger SYSTEM_ERROR_LOG = LoggerFactory.getLogger("SYSTEM_ERROR_LOG");
    /**
     * 三方日志
     */
    private static final Logger SAL_INFO_LOG = LoggerFactory.getLogger("SAL_INFO_LOG");
    /**
     * 三方异常
     */
    private static final Logger SAL_ERROR_LOG = LoggerFactory.getLogger("SAL_ERROR_LOG");

    /**
     * 监控日志
     */
    private static final Logger MONITOR_LOG = LoggerFactory.getLogger("MONITOR_LOG");


    public static void monitor(MonitorType monitorType, MonitorStatus monitorStatus,  IErrorCode iErrorCode, String method, long start, Object... args) {
        StringBuilder sb = new StringBuilder();
        if (ObjectUtil.isNotEmpty(args)) {
            for (Object arg : args) {
                sb.append(",").append(arg);
            }
        }
        String msg = MessageFormat.format("{0},{1},{2},{3},{4},{5},{6},{7}ms",
                method,
                monitorType.name(),
                monitorStatus.name(),
                iErrorCode.getCode(),
                iErrorCode.getIErrorType(),
                iErrorCode.getIErrorLevel(),
                sb.toString(),
                System.currentTimeMillis() - start
                );
        MONITOR_LOG.info(msg);
    }

    public static void printBizInfo(String format, Object...args) {
        BIZ_INFO_LOG.info(format, args);
    }

    public static void printBizError(String format, Object...args) {
        BIZ_ERROR_LOG.error(format, args);
    }

    public static void printSysError(String format, Object...args) {
        SYSTEM_ERROR_LOG.error(format, args);
    }

    public static void printSalInfo(String format, Object... args) {
        SAL_INFO_LOG.info(format, args);
    }

    public static void printSalError(String format, Object...args) {
        SAL_ERROR_LOG.error(format, args);
    }
}
