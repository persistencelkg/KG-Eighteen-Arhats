package org.lkg.combine_demo;

import java.util.Collections;
import java.util.List;

/**
 * @date: 2026/1/24 22:43
 * @author: li kaiguang
 */
public class QueryCarOwnerByCardInputSchame implements ParamInputSchema{

    public static QueryCarOwnerByCardInputSchame INSTANCE = new QueryCarOwnerByCardInputSchame();

    @Override
    public List<ParamEnums> getParam() {
        return Collections.singletonList(ParamEnums.BY_ID_CARD);
    }
}
