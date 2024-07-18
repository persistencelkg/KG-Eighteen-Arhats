package org.lkg.simple;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
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
        if (ObjectUtil.isEmpty(uri)) {
            return uri;
        }
        try {
            //default the ' '(space) char will be inplace '+'
            return URLEncoder.encode(uri, StandardCharsets.UTF_8.name()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decodeUrl(String uri) {
        if (ObjectUtil.isEmpty(uri)) {
            return uri;
        }
        try {
            return URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeURLPath(String urlString) {
        return encodeURLPath(urlString, true, false);
    }

    public static String encodeURLPath(String urlString, boolean encodePath, boolean encodeSuffix) {
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            String path = url.getPath();
            String query = url.getQuery();
            if (encodeSuffix) {
                int i = path.lastIndexOf(File.separator);
                path = path.substring(0, i) + File.separator + encodeUrl(path.substring(i + 1));
            } else if (encodePath) {
                path = encodeUrl(url.getPath());
            }
            StringBuilder encodedUrl = new StringBuilder();
            encodedUrl.append(protocol).append("://").append(host);
            if (port != -1) {
                encodedUrl.append(":").append(port);
            }
            encodedUrl.append(path);
            if (query != null) {
                encodedUrl.append("?").append(query);
            }
            return encodedUrl.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseName(String uri) {
        if (ObjectUtil.isEmpty(uri)) {
            return uri;
        }
        return uri.substring(uri.lastIndexOf(File.separator) + 1);
    }

    public static void main(String[] args) {
        System.out.println(parseName("\\Desk\\user\\aa.txt"));
        System.out.println(parseName("https://www.baidu.com/a.png"));
        // https://d.jjxswshuku.com/d/file/p/txt/2024/%E3%80%8A%E6%88%91%E7%9A%84%E7%8B%AC%E7%AB%8B%E6%97%A5%E3%80%8B%E4%BD%9C%E8%80%85%EF%BC%9A%E5%AE%B9%E5%85%89.txt
        System.out.println(decodeUrl("https://d.jjxswshuku.com/d/file/p/txt/2024/%E3%80%8A%E6%88%91%E7%9A%84%E7%8B%AC%E7%AB%8B%E6%97%A5%E3%80%8B%E4%BD%9C%E8%80%85%EF%BC%9A%E5%AE%B9%E5%85%89.txt"));
        System.out.println(UrlUtil.encodeURLPath("https://d.jjxswshuku.com/d/file/p/txt/2024/《我的独立日》作者：容光.txt", true, true));
        System.out.println(new String("ã\u0080\u008Aæ\u0088\u0091ç\u009A\u0084ç\u008B¬ç«\u008Bæ\u0097¥ã\u0080\u008Bä½\u009Cè\u0080\u0085ï¼\u009Aå®¹å".getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8));
        System.out.println(decodeUrl("https://d.jjxswshuku.com/d/file/p/txt/2024/%C3%A3%C2%80%C2%8A%C3%A6%C2%88%C2%91%C3%A7%C2%9A%C2%84%C3%A7%C2%8B%C2%AC%C3%A7%C2%AB%C2%8B%C3%A6%C2%97%C2%A5%C3%A3%C2%80%C2%8B%C3%A4%C2%BD%C2%9C%C3%A8%C2%80%C2%85%C3%AF%C2%BC%C2%9A%C3%A5%C2%AE%C2%B9%C3%A5%C2%85%C2%89.txt"));
    }

    public static String buildUrl(String url, Map<String, Object> param) {
        url = url.contains("?") ? url + "&" : url + "?";
        url += param.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
        return url;
    }
}
