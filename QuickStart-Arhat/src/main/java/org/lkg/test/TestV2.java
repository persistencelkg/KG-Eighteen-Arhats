package org.lkg.test;
import io.micrometer.core.instrument.Metrics;
import org.apache.kafka.common.Metric;
import org.lkg.bo.QcHolidayDict;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/13 11:40 AM
 */
@RestController
@RequestMapping("/")
public class TestV2 {

    @Value("${config-list:测试,hh,lkg}")
    private List<String> list;


    @Value("${config-set:测试,hh,hh,ff,ff}")
    private Set<String> set;


    @GetMapping("/test-list")
    public String get() {
        System.out.println(list);
        System.out.println(set);
        for (int i = 0; i < 10 ; i++) {
            Metrics.counter("inc" + i).increment(10);
        }

        return  "";
    }


    @Resource private TestDao testDao;

    @GetMapping("/test-mybatis/{id}")
    public String testMybatis(@PathVariable("id") int id) {
        List<QcHolidayDict> qcHolidayDicts = testDao.listData(id);
        System.out.println(qcHolidayDicts);
        return qcHolidayDicts.toString();
    }
}

