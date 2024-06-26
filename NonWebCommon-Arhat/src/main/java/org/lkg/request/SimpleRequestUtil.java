package org.lkg.request;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import org.lkg.simple.JacksonUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/27 7:31 PM
 */
public class SimpleRequestUtil {
    public static SimpleResponse request(SimpleRequest request) {
        SimpleResponse response = new SimpleResponse();
        response.setUrl(request.getUrl());
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(request.getUrl()).openConnection();
            connection.setRequestMethod(request.getMethod());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            request.getHeaders().forEach(connection::addRequestProperty);
            if (request.getBody() != null) {

                OutputStream out = connection.getOutputStream();
                out.write(request.bodyAsBytes());
                out.flush();
                out.close();
            }
            String str = parseInputStream(connection.getInputStream());
            response.setHeader(connection.getHeaderFields());
            response.setStatusCode(connection.getResponseCode());
            connection.getInputStream();
            response.setBody(str);
            connection.disconnect();
        } catch (IOException e) {
            response.setException(e);
        }
        return response;
    }

    private static String parseInputStream(InputStream inputStream) throws IOException {
        byte[] read = new byte[4096];
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream(4096);
        int line = -1;
        BufferedInputStream bufferedReader = new BufferedInputStream(inputStream);
        while ((line = bufferedReader.read(read)) != -1) {
            byteArrayInputStream.write(read, 0, line);
        }
        String string = byteArrayInputStream.toString();
        byteArrayInputStream.close();
        inputStream.close();
        return string;
    }

    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("url", "{{test-sgcx-pay-data-sync-8088}}/sys/");
        SimpleResponse response = request(SimpleRequest.createGetRequest("http://dev-inside-gray-admin.songguo7.com/gray-server-admin/health/ip", SimpleRequest.BodyEnum.RAW, map));
        System.out.println(response);
    }
}
