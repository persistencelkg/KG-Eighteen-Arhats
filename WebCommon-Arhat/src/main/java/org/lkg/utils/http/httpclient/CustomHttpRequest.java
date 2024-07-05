package org.lkg.utils.http.httpclient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.lkg.request.InternalRequest;
import org.lkg.simple.ObjectUtil;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:07 AM
 */

@Getter
public class CustomHttpRequest extends HttpEntityEnclosingRequestBase {

    private final InternalRequest internalRequest;

    private final String serverName;

    public CustomHttpRequest(InternalRequest request, String serverName) {
        super();
        this.internalRequest = request;
        this.serverName = serverName;
        setURI(URI.create(internalRequest.getUrl()));
        if (Objects.equals(request.getMethod(), HttpMethod.POST.name()) && !ObjectUtil.isEmpty(request.bodyAsString())) {
            StringEntity stringEntity = new StringEntity(request.bodyAsString(), StandardCharsets.UTF_8);
            setEntity(stringEntity);
        }
    }

    @Override
    public String getMethod() {
        return internalRequest.getMethod();
    }
}
