package org.lkg;

import lombok.Data;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.lkg.elastic_search.crud.EsMetaApIService;
import org.lkg.elastic_search.crud.MapDataEsApIService;
import org.lkg.elastic_search.crud.demo.Orders;
import org.lkg.elastic_search.enums.EsDoc;
import org.lkg.redis.crud.RedisService;
import org.lkg.redis.crud.TestInterFace;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.lkg.redis.crud.RedisService.DYNAMIC_UPDATE_BY_LUA;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/5 4:41 PM
 */
public class TestNoSql extends TestBase {

    @Resource
    private EsMetaApIService esMetaApIService;

    @Resource
    private MapDataEsApIService<Orders> mapDataEsApIService;

    @Resource
    private RestHighLevelClient order;
//
//    @Resource
//    private RedisTemplate<String, Object> featureRedisTemplate;
//
//    @Resource
//    private RedisTemplate<String, Object> orderRedisTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private TestInterFace testOne;

    @Resource
    private TestInterFace testTwo;

    @EsDoc(type = "orders", uniqueKey = "id")
    @Data
    private static class TestMap {
        private Map<String, Object> map;
    }

    @Test
    public void testEsApi() {
//        System.out.println(esMetaApIService.createIndex(order, Orders.class));
//        System.out.println(esMetaApIService.existIndex(order, Orders.class));
//        System.out.println(esMetaApIService.addColumnForIndex(order, "_doc", Orders.class));
//        System.out.println(esMetaApIService.createOrUpdateIndexTemplate(order,"order_tmpl", "order_*", Orders.class));
        Orders orders = new Orders();
        orders.setAge(13L);
        orders.setName("lkg");
//        orders.setFee(BigDecimal.TEN);
        orders.setStartTime(new Date(System.currentTimeMillis()));

//        System.out.println(mapDataEsApIService.saveOrUpdateDocument(order, "orders", "2", JacksonUtil.objToMap(orders)));
        Orders orders1 = mapDataEsApIService.getDocument(order, Orders.class, "2");
        System.out.println(orders1);
        Map<String, Object> map = mapDataEsApIService.getDocumentMap(order, Orders.class, "2");
        System.out.println(map);
    }


    @Test
    public void testRedis() {
        System.out.println("testOne" + testOne + " =>" + testTwo);
        Orders orders = new Orders();
        orders.setAge(3L);
        orders.setName(null);
//        orders.setFee(BigDecimal.TEN);
        orders.setStartTime(new Date(System.currentTimeMillis()));

        // test set
        redisService.setKeyWithSecond("test-lkg", "wkx", 846000L);
        // test distribute lock
        boolean l1 = redisService.getLock("lock-key", "0", 1L);
        boolean l2 = redisService.getLock("lock-key", "0", 1L);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        boolean l3 = redisService.getLock("lock-key", "0", 1L);
        System.out.println(l1 + "->" + l2 + "->" + l3);
        // test del
        System.out.println("del key: " + redisService.delKey("test-lkg"));
        // test hash
        redisService.hSet("lkg-2", "love", "wkx");
        String s = redisService.hGet("lkg-2", "love", String.class);
        System.out.println("hget:" + s);

        // tes lua
        redisService.hSet("lkg-2", "wkx-lkg", 999);
        HashMap<String, Object> map = new HashMap<>();
        map.put("wkx-lkg", 99);
        map.put("lua", "nb");
        System.out.println("update lua count: " + redisService.execWithLua(DYNAMIC_UPDATE_BY_LUA, "lkg-2", map));

        String s2 = redisService.hGet("lkg-2", "wkx-lkg", String.class);
        System.out.println("hget:" + s2);


    }
}
