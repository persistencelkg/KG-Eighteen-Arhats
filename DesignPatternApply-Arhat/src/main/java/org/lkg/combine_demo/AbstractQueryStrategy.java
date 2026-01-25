package org.lkg.combine_demo;

import java.util.Map;

/**
 * @date: 2026/1/22 23:39
 * @author: li kaiguang
 */
public abstract class AbstractQueryStrategy<T> implements QueryStrategy<T> {

    @Override
    public T queryResult(Map<String, Object> jsonMap) {
        // 先判断查询策略

        // 根据查询策略 确定执行结果
        // 1. 先查缓存
        T t = queryFromCache();
        if (t == null) {
            t = queryFromRealTime();
        }
        // 2. 再查实时数据源

        // 3. 写入缓存
        if (t != null) {
            writeCache(t);
        }

        return null;
    }

    protected void writeCache(T t) {};

    protected abstract T queryFromRealTime();

    protected abstract T queryFromCache();
}
