package org.lkg.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import feign.Request;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.util.TimeUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.Metric;
import org.aspectj.weaver.ast.Test;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.lkg.bo.QcHolidayDict;
import org.lkg.bo.User;
import org.lkg.elastic_search.crud.EsMetaApIService;
import org.lkg.elastic_search.crud.MapDataEsApIService;
import org.lkg.elastic_search.crud.demo.Orders;
import org.lkg.feign.TestFeign;
import org.lkg.kafka.biz.KafkaService;
import org.lkg.redis.crud.RedisService;
import org.lkg.request.InternalRequest;
import org.lkg.request.InternalResponse;
import org.lkg.rocketmq.biz.MqRetryConfigValue;
import org.lkg.rocketmq.biz.MqRetrySendService;
import org.lkg.utils.http.httpclient.HttpClientUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.lkg.redis.crud.RedisService.DYNAMIC_UPDATE_BY_LUA;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/13 11:40 AM
 */
@RestController
@Slf4j
@RequestMapping("/")
public class TestV2 implements InitializingBean {

    @Value("${config-list:测试,hh,lkg}")
    private List<String> list;


    @Value("${config-set:测试,hh,hh,ff,ff}")
    private Set<String> set;

    @Resource
    private ExecutorService kgService;

    @GetMapping("/test-list")
    public String get() {


        kgService.execute(() -> {
            // test http
            InternalRequest postRequest = InternalRequest.createPostRequest("https://oapi.dingtalk.com/robot/send?access_token=37c083e9fffc155f5a5014cca52f01a07c8fee318da79e9a3f339bfd6a102e98", InternalRequest.BodyEnum.RAW);
            InternalResponse server = HttpClientUtil.invoke("server", postRequest);
            log.info(">>> server:{}", server);
        });

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }

        return "true";
    }


    @Resource
    private TestDao testDao;

    @Resource
    private TestMpService testMpService;

    @GetMapping("/test-mybatis/{id}")
    public String testMybatis(@PathVariable("id") int id) {
        List<QcHolidayDict> qcHolidayDicts = testDao.listData(id);
        long aLong = (int) (Math.random() * 1000000);
        System.out.println(testDao.insertDict(new User(aLong, UUID.randomUUID().toString(), "xxx", 1)));
        // insert
        System.out.println("test mybatis plus ------->");
        ArrayList<User> objects = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            aLong = (int) (Math.random() * 1000000);
            objects.add(new User(aLong, UUID.randomUUID().toString(), "wlkx",
                    (int) (Math.random() * 100)));

        }
        testMpService.saveBatch(objects);
        return qcHolidayDicts.toString();
    }

    @Resource
    private RedisService redisService;

    @GetMapping("/test-redis")
    public boolean testRedis() {
        redisService.setKey("lkg", "wkx", 1, TimeUnit.SECONDS);
        System.out.println(redisService.getKey("lkg", String.class));
        HashMap<String, Object> map = new HashMap<>();
        map.put("wkx-lkg", 99);
        map.put("lua", "nb");
        System.out.println("update lua count: " + redisService.execWithLua(DYNAMIC_UPDATE_BY_LUA, "lkg-2", map));

        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisService.getKey("lkg", String.class);
    }


    @Resource
    private TestFeign testFeign;

    @GetMapping("/test-feign")
    public boolean testFeign() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("params", new HashMap<String, Object>() {{
            put("user_id", 1L);
        }});
        log.info("test: param");
//        testFeign.testId(map);
        log.info("{}", (testFeign.getUserCard(map, new Request.Options(23, TimeUnit.MILLISECONDS, 101, TimeUnit.MILLISECONDS, true))));
        return true;
    }


    @Resource
    private KafkaService kafkaService;

    @GetMapping("/test-kafka/{topic}")
    public boolean sendMsg(@PathVariable("topic") String topic) {
        kafkaService.sendMsg(topic, "随机消息:" + UUID.randomUUID());
        return true;
    }

    @Resource
    private MqRetrySendService mqRetrySendService;

    @GetMapping("/test-rocket/{topic}")
    public boolean sendRocketMsg(@PathVariable("topic") String topic) {
        mqRetrySendService.sendWithRetry(topic, "随机rocket消息:" + UUID.randomUUID());
        mqRetrySendService.asyncSendWithRetry(topic, "异步随机rocket消息:" + UUID.randomUUID());
        return true;
    }



    @Resource
    private MapDataEsApIService<Orders> mapDataEsApIService;

    @Resource
    private EsMetaApIService esMetaApIService;

    @Resource
    private RestHighLevelClient order;

    @GetMapping("/test-es")
    public Object testEs() {

//        System.out.println(esMetaApIService.createIndex(order, Tes.class));

        ArrayList<Orders> list = new ArrayList<>();
        Orders orders = new Orders();
        orders.setId(2);
        orders.setName("lkg");
        orders.setText("wkx");
        list.add(orders);

        Orders o2 = new Orders();
        o2.setId(3);
        o2.setText("测试");
        list.add(o2);
        mapDataEsApIService.batchUpdateDocument(order,list, false, DocWriteRequest.OpType.UPDATE);

        List<Orders> orders2 = mapDataEsApIService.multiGetDocument(order, Orders.class, Lists.newArrayList("2", "3"));
        System.out.println(orders2);
        Orders orders1 = mapDataEsApIService.getDocument(order, Orders.class, "2");
        System.out.println(orders1);
//        Map<String, Object> map = mapDataEsApIService.getDocumentMap(order, Orders.class, "2");
        return orders1;
    }

    @Data
    static class Tes {
        private String a;

        private String url;
    }
}

