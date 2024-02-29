package org.lkg.simple;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/28 5:26 PM
 */
public class UrlUtil {

    public static String buildUrl(String url, Map<String, Object> param) {
        url = url.contains("?") ? url + "&" : url + "?";
        url += param.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
        return url;
    }
}
