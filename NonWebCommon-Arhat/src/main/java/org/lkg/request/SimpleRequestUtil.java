package org.lkg.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.lkg.utils.IOStreamUtil;
import org.lkg.utils.ObjectUtil;
import org.lkg.utils.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/27 7:31 PM
 */


public class SimpleRequestUtil {

    private static final Logger log = LoggerFactory.getLogger(SimpleRequestUtil.class.getSimpleName());

    public static InternalResponse request(InternalRequest request) {
        return request(request, null, null, true);
    }


    public static InternalResponse request(InternalRequest request, boolean followRedirect) {
        return request(request, null, null, followRedirect);
    }

    public static InternalResponse requestDefaultTimeout(InternalRequest request, boolean followRedirect) {
        return request(request, Math.toIntExact(request.getConnectionTimeout()), Math.toIntExact(request.getReadTimeout()), followRedirect);
    }

    public static InternalResponse request(InternalRequest request, Integer connectionTimeOut, Integer readTimeout, boolean followRedirect) {
        InternalResponse response = new InternalResponse(request);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(request.getUrl()).openConnection();
            connection.setRequestMethod(request.getMethod());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            // 不跟随重定向，如果有单独处理
            if (!followRedirect) {
                connection.setInstanceFollowRedirects(false);
                RedirectInfo redirectInfo = followRedirects(connection);
                connection = redirectInfo.getHttpURLConnection();
                String lastUrl = redirectInfo.getRedirectUrl().pollLast();
                if (ObjectUtil.isEmpty(lastUrl)) {
                    lastUrl = connection.getURL().toString();
                    // 真实的url
                    response.setLatestResponseUrl(UrlUtil.decodeUrl(lastUrl));
                }
            }

            if (Objects.nonNull(connectionTimeOut) && Objects.nonNull(readTimeout) && connectionTimeOut <= readTimeout) {
                connection.setConnectTimeout(connectionTimeOut);
                connection.setReadTimeout(readTimeout);
            }

            if (!ObjectUtil.isEmpty(request.getHeaders())) {
                request.getHeaders().forEach(connection::addRequestProperty);
            }
            // 封装请求
            if (request.getBody() != null) {
                OutputStream out = connection.getOutputStream();
                out.write(request.bodyAsBytes());
                out.flush();
                out.close();
            }

            // 解析
            byte[] str = parseInputStream(connection.getInputStream());
            int responseCode = connection.getResponseCode();
            response.setStatusCode(responseCode);
            response.setResult(new String(str, StandardCharsets.UTF_8));
            response.setResultBytes(str);
            connection.disconnect();
        } catch (IOException e) {
            response.addException(e);
        }
        return response;
    }

    private static byte[] parseInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        int line = -1;
        byte[] bytes = new byte[IOStreamUtil.BUFFER_8_K];
        // default 8k
//        FileOutputStream fileOutputStream = new FileOutputStream("t.jpg");
//        IOStreamUtil.copy(inputStream, fileOutputStream);
        BufferedInputStream bufferedReader = new BufferedInputStream(inputStream);
        while ((line = bufferedReader.read(bytes)) != -1) {
            arrayOutputStream.write(bytes, 0, line);
        }
        // 多媒体数据必须采用字节数组方式写入流中，不能做字符串转换，否则会出现文件损坏的情况【文本除外】
//        String string = arrayOutputStream.toString(StandardCharsets.UTF_8.name());
//        IOStreamUtil.close(arrayOutputStream, bufferedReader);
        return arrayOutputStream.toByteArray();
    }


    public static RedirectInfo followRedirects(URLConnection var0) throws IOException {
        int var2 = 0;
        LinkedList<String> redirectUrlMap = new LinkedList<>();
        boolean var1;
        do {
            if (var2 > 15) {
                throw new RemoteException("too much redirect");
            }
            if (var0 instanceof HttpURLConnection) {
                ((HttpURLConnection) var0).setInstanceFollowRedirects(false);
            }
            var0.setConnectTimeout(10000);
            var0.setReadTimeout(60000);

            var1 = false;
            if (var0 instanceof HttpURLConnection) {
                HttpURLConnection var4 = (HttpURLConnection) var0;
                int var5 = var4.getResponseCode();
                redirectUrlMap.add(var4.getURL().toString());
                if (isRedirect(var5)) {
                    URL var6 = var4.getURL();
                    String var7 = var4.getHeaderField("Location");
                    URL var8 = null;
                    if (var7 != null) {
                        var8 = new URL(var6, UrlUtil.encodeURLPath(new String(var7.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8), false, true));
                    }

                    if (var6 != null && var8 != null && !Objects.equals(var6, var8)) {
                        redirectUrlMap.add(var8.toString());
                    }

                    cleanupConnection(var4);
                    if (var8 == null || !var6.getProtocol().equals(var8.getProtocol()) || var2 >= 5) {
                        throw new SecurityException("illegal URL redirect");
                    }

                    var1 = true;
                    var0 = var8.openConnection();
                    ++var2;
                }
            }
        } while (var1);
//        System.out.println(redirectUrlMap);
        if (log.isDebugEnabled()) {
            log.debug("pass location:{}", redirectUrlMap);
        }
        if (!(var0 instanceof HttpURLConnection)) {
            throw new IOException(var0.getURL() + " redirected to non-http URL");
        } else {
            return new RedirectInfo(redirectUrlMap, (HttpURLConnection) var0);
        }
    }


    public static boolean isRedirect(int var0) {
        return var0 >= 300 && var0 <= 305 && var0 != 304;
    }

    @Data
    @AllArgsConstructor
    private static class RedirectInfo {
        private LinkedList<String> redirectUrl;
        private HttpURLConnection httpURLConnection;
    }


    public static void cleanupConnection(URLConnection var0) {
        if (var0 instanceof HttpURLConnection) {
            try {
                HttpURLConnection var1 = (HttpURLConnection) var0;
                String var2 = var1.getHeaderField((String) null);
                String var3 = var1.getHeaderField("Connection");
                if (var3 != null && var3.equalsIgnoreCase("Keep-Alive") || var2 != null && var2.startsWith("HTTP/1.1") && var3 == null) {
                    int var4 = var1.getResponseCode();
                    if (var4 < 400) {
                        InputStream var5 = var1.getInputStream();
                        if (var5 != null) {
                            byte[] var6 = new byte[8192];
                            while (true) {
                                if (var5.read(var6) <= 0) {
                                    var5.close();
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (IOException var7) {
            }

        }
    }

    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
//        map.put("url", "{{test-sgcx-pay-data-sync-8088}}/sys/");
        InternalResponse response = request(InternalRequest.createGetRequest("https://tools.liumingye.cn/music/#/", InternalRequest.BodyEnum.HTML, null));

        System.out.println(response.getResult());
//        System.out.println(response);
        String s = UrlUtil.encodeUrl("http://dev-inside.com/ 1");
        System.out.println();
        System.out.println(UrlUtil.decodeUrl(s));


    }
}
