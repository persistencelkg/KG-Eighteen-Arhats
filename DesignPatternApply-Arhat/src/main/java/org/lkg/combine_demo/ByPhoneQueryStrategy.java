package org.lkg.combine_demo;

import java.util.Map;

/**
 * @date: 2026/1/22 23:50
 * @author: li kaiguang
 */
public class ByPhoneQueryStrategy extends AbstractQueryStrategy<String>
{
    @Override
    protected String queryFromRealTime() {
        return null;
    }

    @Override
    protected String queryFromCache() {
        return null;
    }

    @Override
    public ParamInputSchema getCode() {
        return QueryCarOwnerByCardInputSchame.INSTANCE;
    }

    @Override
    public String queryResult(Map<String, Object> jsonMap) {
        return null;
    }
}
