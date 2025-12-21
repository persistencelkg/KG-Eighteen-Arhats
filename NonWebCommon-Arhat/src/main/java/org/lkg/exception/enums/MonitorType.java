package org.lkg.exception.enums;

import lombok.Getter;

/**
 * @description: 监控类型，需要明确具体监控的数据
 * @date: 2025/12/13 21:05
 * @author: li kaiguang
 */
@Getter
public enum MonitorType {

    /**
     * webapi 接口
     */
    WEB,

    /**
     * 调用2方包，基于远程过程调用如Nacos、Dubble、TR
     */
    RPC,


    /**
     * SAL 外部通信
     * 如http请求
     */
    SAL_HTTP,

    /**
     * INNER 应用调用
     */
    INNER,


    /**
     * MQ
     */

    MQ,

    /**
     * TASK
     */

    TASK
    ;
}
