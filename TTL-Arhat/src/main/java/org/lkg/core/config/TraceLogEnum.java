package org.lkg.core.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/18 4:05 PM
 */
@Getter
@AllArgsConstructor
public enum TraceLogEnum {

    Feign("third"),
    MySQL("sql"),
    Redis("redis"),
    ElasticSearch("es"),
    MongoDB("mongo"),
    TiDB("tidb")
    ;

    private final String namespacePrefix;

    public String getNameSpace(boolean res) {
        return namespacePrefix + (res ? ".suc": ".fail");
    }

    public String getTraceTimeOutKey() {
        return namespacePrefix + ".suc.tc-tt";
    }
}
