package org.lkg;

import org.junit.Test;
import org.lkg.request.InternalRequest;
import org.lkg.request.InternalResponse;
import org.lkg.utils.http.httpclient.HttpClientUtil;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 4:23 PM
 */
public class TestCustomClient extends TestBase{


    @Test
    public void testHttpClient() {

        InternalRequest postRequest = InternalRequest.createPostRequest("https://oapi.dingtalk.com/robot/send?access_token=37c083e9fffc155f5a5014cca52f01a07c8fee318da79e9a3f339bfd6a102e98", InternalRequest.BodyEnum.RAW);
        InternalResponse server = HttpClientUtil.invoke("server", postRequest);
        System.out.println(server);
    }
}
