package org.lkg;

import org.aspectj.weaver.ast.Or;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.lkg.elastic_search.crud.EsMetaApIService;
import org.lkg.elastic_search.crud.EsMetaApIServiceImpl;
import org.lkg.elastic_search.crud.MapDataEsApIService;
import org.lkg.elastic_search.crud.demo.Orders;
import org.lkg.redis.config.RedisTemplateHolder;
import org.lkg.simple.JacksonUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

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

    @Resource
    private RedisTemplateHolder redisTemplateHolder;
//
//    @Resource
//    private RedisTemplate<String, Object> featureRedisTemplate;
//
//    @Resource
//    private RedisTemplate<String, Object> orderRedisTemplate;



    @Test
    public void testEsApi() {
        esMetaApIService.createIndex(order, "order", Orders.class);
    }

    @Test
    public void testRedis() {
        Orders orders = new Orders();
        orders.setAge(3);
        orders.setName("测试wkx");
        orders.setFee(BigDecimal.TEN);
        orders.setStartTime(new Date(System.currentTimeMillis()));

        RedisTemplate<String, Object> featureRedisTemplate = redisTemplateHolder.featureTemplate();
        RedisTemplate<String, Object> orderRedisTemplate = redisTemplateHolder.orderTemplate();

        ValueOperations<String, Object> opsForValue = featureRedisTemplate.opsForValue();
        opsForValue.set("test-lkg", orders);

        Object obj = opsForValue.get("test-lkg");
        Orders orders1 = JacksonUtil.getMapper().convertValue(obj, Orders.class);
        System.out.println(orders1);

        ValueOperations<String, Object> operations = orderRedisTemplate.opsForValue();
//        operations.getOperations()
        System.out.println(operations.setIfAbsent("wkx", "NIL"));
        System.out.println(operations.setIfAbsent("wkx", "2"));

    }
}
