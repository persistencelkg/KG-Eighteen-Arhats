package org.lkg.test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

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

    @GetMapping("/test-list")
    public String get() {
        return Arrays.toString(list.toArray(new String[0]));
    }
}
