package org.lkg.test;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.util.TimeUtils;
import org.apache.kafka.common.Metric;
import org.lkg.bo.QcHolidayDict;
import org.lkg.bo.User;
import org.lkg.redis.crud.RedisService;
import org.lkg.request.InternalRequest;
import org.lkg.request.InternalResponse;
import org.lkg.utils.http.httpclient.HttpClientUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
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
//        kgService.execute(()-> {
//            synchronized (TestV2.class) {
//                try {
//                    TimeUnit.SECONDS.sleep(20);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                System.out.println("锁释放--------");
//            }
//        });

        // test http
        InternalRequest postRequest = InternalRequest.createPostRequest("https://oapi.dingtalk.com/robot/send?access_token=37c083e9fffc155f5a5014cca52f01a07c8fee318da79e9a3f339bfd6a102e98", InternalRequest.BodyEnum.RAW);
        InternalResponse server = HttpClientUtil.invoke("server", postRequest);

        return  server.toString();
    }


    @Resource private TestDao testDao;

    @Resource private TestMpService testMpService;

    @GetMapping("/test-mybatis/{id}")
    public String testMybatis(@PathVariable("id") int id) {
        List<QcHolidayDict> qcHolidayDicts = testDao.listData(id);
        System.out.println(testDao.insertDict(new User(UUID.randomUUID().toString(), "xxx", 1)));
        // insert
        System.out.println("test mybatis plus ------->");
        System.out.println(testMpService.saveBatch(Lists.newArrayList(
                new User(UUID.randomUUID().toString(), "wlkx",
                        (int) (Math.random() * 100)), new User(UUID.randomUUID().toString(), "wlkx", (int) (Math.random() * 100)))));
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.ge(User::getAge, 1);
//        System.out.println(testMpDao.selectList(queryWrapper));
        return qcHolidayDicts.toString();
    }

    @Resource private RedisService redisService;

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
}

