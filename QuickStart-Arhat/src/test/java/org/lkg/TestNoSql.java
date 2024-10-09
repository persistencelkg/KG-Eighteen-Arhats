package org.lkg;

import org.aspectj.weaver.ast.Or;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.lkg.elastic_search.crud.EsMetaApIService;
import org.lkg.elastic_search.crud.EsMetaApIServiceImpl;
import org.lkg.elastic_search.crud.MapDataEsApIService;
import org.lkg.elastic_search.crud.demo.Orders;
import org.lkg.redis.config.RedisTemplateHolder;
import org.lkg.redis.crud.RedisService;
import org.lkg.redis.crud.TestInterFace;
import org.lkg.simple.JacksonUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.lkg.redis.crud.RedisService.DYNAMIC_UPDATE_BY_LUA;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/5 4:41 PM
 */
public class TestNoSql extends TestBase {

    @Resource
    private EsMetaApIService<Orders> esMetaApIService;

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


    @Test
    public void testEsApi() {
//        System.out.println(esMetaApIService.createIndex(order, Orders.class));
//        System.out.println(esMetaApIService.existIndex(order, Orders.class));
//        System.out.println(esMetaApIService.addColumnForIndex(order, "_doc", Orders.class));
        System.out.println("----- 上面测试都是通过的 ---------");

        System.out.println(esMetaApIService.createOrUpdateIndexTemplate(order,"order_tmpl", "order_*", Orders.class));
    }

    @Test
    public void testRedis() {
        System.out.println("testOne" + testOne + " =>" + testTwo);
        Orders orders = new Orders();
        orders.setAge(3);
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
