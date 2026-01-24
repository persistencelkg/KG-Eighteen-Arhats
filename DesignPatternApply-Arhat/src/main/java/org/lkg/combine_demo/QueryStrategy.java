package org.lkg.combine_demo;

import java.util.Map;

/**
 * @description:
 * @date: 2026/1/22 23:38
 * @author: li kaiguang
 */
public interface QueryStrategy<T> {

    ParamInputSchema getCode();

    T queryResult(Map<String, Object> jsonMap);
}
