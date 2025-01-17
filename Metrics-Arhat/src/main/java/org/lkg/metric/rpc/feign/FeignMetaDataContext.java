package org.lkg.metric.rpc.feign;

import feign.MethodMetadata;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.lkg.utils.matcher.AntPathMatcher;
import org.springframework.web.util.UriTemplate;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/9 7:37 PM
 */
public class FeignMetaDataContext {

    // Ag: key http://atm-coupon/atm-coupon/a/bc/ or http://127.0.0.1[set absolute url]/atm-coupon/a/bc/
    private static final ConcurrentHashMap<String, FeignMetaData> SERVER_URL_CONTEXT = new ConcurrentHashMap<>();
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    /**
     * @param logicFullUrl 逻辑全路径
     * @param metadata     对应的feign方法的元数据
     */
    public static void addFeignUrl(String logicFullUrl, MethodMetadata metadata) {
        String substring = logicFullUrl.substring(logicFullUrl.indexOf(File.separator) + 2);
        String server = substring.substring(0, substring.indexOf(File.separator));
        String uri = substring.substring(substring.indexOf(File.separator));
        String patternUrl = File.separator + "**".concat(uri);
        RequestTemplate template = metadata.template();
        // 对url做检查
        SERVER_URL_CONTEXT.put(patternUrl, new FeignMetaData(server, template.method(), uri));
    }

    public static FeignMetaData getFeignMetaContext(String logicFullUrl) {
        String removeCommonPrefix = logicFullUrl.substring(logicFullUrl.indexOf(File.separator) + 1);
        for (String pattern : SERVER_URL_CONTEXT.keySet()) {
            if (pathMatcher.match(pattern, removeCommonPrefix)) {
                return SERVER_URL_CONTEXT.get(pattern);
            }
        }
        return null;
    }

    public static void clear(String url) {
        SERVER_URL_CONTEXT.remove(url);
    }


    public static void main(String[] args) {
        System.out.println(new UriTemplate("/a/atm/{id}/a/{c}").matches("/a/atm/c/a/d"));
        System.out.println(new UriTemplate("**/a/atm/{id}/a/{c}").getVariableNames());
        System.out.println(new AntPathMatcher().match("/**/aa/atm-coupon/{a:\\w+}", "/c/c5a/d/aa/atm-coupon/a333"));
//        addFeignUrl("http://atm-coupon/atm-coupon/a/bc/", null);
//        System.out.println(SERVER_URL_CONTEXT.get());
    }


    @Data
    @AllArgsConstructor
    public  static class FeignMetaData {
        private String server;
        private String method;
        // only without domain
        private String uri;
    }
}
