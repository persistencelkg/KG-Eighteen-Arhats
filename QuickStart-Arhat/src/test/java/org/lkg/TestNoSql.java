package org.lkg;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.lkg.elastic_search.crud.AbstractEsApI;

import javax.annotation.Resource;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/5 4:41 PM
 */
public class TestNoSql extends TestBase{

    @Resource
    private AbstractEsApI abstractEsApI;

    @Resource
    private RestHighLevelClient order;

    @Test
    public void testEsApi() {
        System.out.println(order);
    }
}
