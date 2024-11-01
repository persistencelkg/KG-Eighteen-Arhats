package io.github.persistence;

import lombok.Data;
import org.lkg.enums.StringEnum;

import java.util.HashMap;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/1 5:01 PM
 */
@Data
public class LongPoolConfig {

    /**
     * 建议是域名
     */
    private String domain;

    private String pollUrl;

    private String longLinkUrl;

    private boolean https;

    private String accessKey;

    private String version;


    public String getPollUrl() {
        return (https ? StringEnum.HTTPS_PREFIX : StringEnum.HTTP_PREFIX) + domain + pollUrl;
    }

    public String getLongLinkUrl() {
        return (https ? StringEnum.HTTPS_PREFIX : StringEnum.HTTP_PREFIX) + domain + longLinkUrl;
    }

    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("test", 22);
        map.put("hh", 11);

        HashMap<String, Object> stringObjectHashMap = new HashMap<>(map);
        stringObjectHashMap.clear();
        System.out.println(map);
    }


}
