package org.lkg.combine_demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date: 2026/1/24 22:58
 * @author: li kaiguang
 */
public class QueryStrategyFactory {

    public static final Map<ParamInputSchema, QueryStrategy<?>> FACTORY = new ConcurrentHashMap<>();
}
