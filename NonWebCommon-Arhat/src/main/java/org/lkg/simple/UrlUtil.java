package org.lkg.simple;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/28 5:26 PM
 */
public class UrlUtil {

    public static String encodeUrl(String uri) {
        try {
            return URLEncoder.encode(uri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decodeUrl(String uri) {
        try {
            return URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(encodeUrl("http://www.baidu.com:8070/config.html#/appid=order-public&env=TEST&cluster=default"));
        System.out.println(decodeUrl("%3A %3B %3C %3D"));
    }

    public static String buildUrl(String url, Map<String, Object> param) {
        url = url.contains("?") ? url + "&" : url + "?";
        url += param.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
        return url;
    }
}
