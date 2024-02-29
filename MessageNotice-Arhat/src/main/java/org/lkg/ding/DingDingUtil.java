package org.lkg.ding;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.lkg.request.SimpleRequest;
import org.lkg.request.SimpleRequestUtil;
import org.lkg.request.SimpleResponse;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;
import org.lkg.simple.UrlUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/28 5:05 PM
 */
@Slf4j
public class DingDingUtil {

    public static final String SEND_URL = "https://oapi.dingtalk.com/robot/send";
    public static final String REFRESH_TOKEN_URL = "https://oapi.dingtalk.com/v1.0/oauth2/accessToken";

    private static final String ERR_MSG = "errmsg";
    private static final String ERR_CODE = "errcode";

    private static String ACCESS_TOKEN = "";

    private static long EXPIRED_IN = 1;

    private static void refreshToken(String appKey, String appSecret) {
        if (!isExpired()) {
            return;
        }
        System.out.println("已经过期");
        HashMap<String, Object> map = new HashMap<>();
        map.put("appKey", appKey);
        map.put("appSecret", appSecret);
        SimpleResponse response = SimpleRequestUtil.request(SimpleRequest.createPostRequest(REFRESH_TOKEN_URL, SimpleRequest.BodyEnum.RAW, map));
        System.out.println(response);
        HashMap<String, Object> result = JacksonUtil.readObj(response.getBody(), new TypeReference<HashMap<String, Object>>() {
        });
        Long aLong = (Long) result.get("expireIn");
        EXPIRED_IN = System.currentTimeMillis() + aLong;
        ACCESS_TOKEN = result.get("accessToken").toString();
        System.out.println(response);
    }

    public static void sendMessage(DingDingMsg dingDingMsg, String url) {
        sendMessage(dingDingMsg, url, null, false);
    }

    public static void sendMessage(DingDingMsg dingDingMsg, String url, String secret) {
        sendMessage(dingDingMsg, url, secret, false);
    }


    public static void sendMessage(DingDingMsg dingDingMsg, String url, String secret, boolean atAll, String... at) {
        try {
            String finalUrl = buildUrl(url, secret);
            SimpleResponse response = SimpleRequestUtil.request(SimpleRequest.createPostRequest(finalUrl, SimpleRequest.BodyEnum.RAW, dingDingMsg.buildRequestBody(atAll, at)));
            response.isSuccess(ERR_CODE, 0);
        } catch (Exception e) {
            log.warn("send ding fail, reason:", e);
        }

    }

    private static boolean isExpired() {
        return EXPIRED_IN < System.currentTimeMillis();
    }

    private static String buildUrl(String url, String secret) {
        if (ObjectUtil.isEmpty(secret)) {
            return url;
        }
        HashMap<String, Object> map = new HashMap<>();
        long now = System.currentTimeMillis();
        map.put("sign", buildSign(secret, now));
        map.put("timestamp", now);
        return UrlUtil.buildUrl(url, map);
    }


    private static String buildSign(String secret, Long timestamp) {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        refreshToken("dingclcqsdnhsf1xnnt4", "MIeuo8HjphWoMdEMPMuTHdPK0wd4XdexzV_A4goIWE-q_neYTzYmY_KlYBlb_K7u");
    }

}
