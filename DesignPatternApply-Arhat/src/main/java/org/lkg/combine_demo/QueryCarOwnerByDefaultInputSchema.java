package org.lkg.combine_demo;

import org.lkg.utils.CollectionUtil;

import java.util.List;

/**
 * @date: 2026/1/24 22:45
 * @author: li kaiguang
 */
public class QueryCarOwnerByDefaultInputSchema implements ParamInputSchema{

    public static QueryCarOwnerByDefaultInputSchema INSTANCE = new QueryCarOwnerByDefaultInputSchema();
    @Override
    public List<ParamEnums> getParam() {
        return CollectionUtil.of(ParamEnums.BY_ID_CARD, ParamEnums.BY_MOBILE);
    }
}
