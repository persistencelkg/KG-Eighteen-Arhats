package org.lkg;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.lkg.elastic_search.crud.EsMetaApIService;
import org.lkg.elastic_search.crud.EsMetaApIServiceImpl;
import org.lkg.elastic_search.crud.MapDataEsApIService;
import org.lkg.elastic_search.crud.demo.Orders;

import javax.annotation.Resource;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/5 4:41 PM
 */
public class TestNoSql extends TestBase{

    @Resource
    private EsMetaApIService<Orders> esMetaApIService;

    @Resource
    private RestHighLevelClient order;

    @Test
    public void testEsApi() {
        esMetaApIService.createIndex(order, "order", Orders.class);
    }
}
