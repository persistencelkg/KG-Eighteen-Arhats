package org.lkg.dynamic_json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date: 2026/1/11 23:32
 * @author: li kaiguang
 */
@Data
@AllArgsConstructor
public class User {

    private String name;

    private Integer age;

    private Address address;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String cityName;
        private String provinceName;
    }

    public static void main(String[] args) {
        System.out.println(Address.class);
    }
}
