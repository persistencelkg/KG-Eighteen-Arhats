package org.lkg.combine_demo;

/**
 * @date: 2026/1/22 23:47
 * @author: li kaiguang
 * 根据手机号或者身份证号查询
 */
public class DefaultQueryStrategy extends AbstractQueryStrategy<String>{
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
        return QueryCarOwnerByDefaultInputSchema.INSTANCE;
    }
}
