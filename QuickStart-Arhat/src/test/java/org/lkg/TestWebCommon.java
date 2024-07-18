package org.lkg;

import org.junit.Test;
import org.lkg.request.InternalRequest;
import org.lkg.request.InternalResponse;
import org.lkg.up_download.down.DownloadService;
import org.lkg.utils.http.httpclient.HttpClientUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 4:23 PM
 */
public class TestWebCommon extends TestBase{


    @Test
    public void testHttpClient() {

        InternalRequest postRequest = InternalRequest.createPostRequest("https://oapi.dingtalk.com/robot/send?access_token=37c083e9fffc155f5a5014cca52f01a07c8fee318da79e9a3f339bfd6a102e98", InternalRequest.BodyEnum.RAW);
        InternalResponse server = HttpClientUtil.invoke("server", postRequest);
        System.out.println(server);
    }


    @Resource
    private DownloadService downloadService;

    @Test
    public void testDownLoad() {

    }
}
