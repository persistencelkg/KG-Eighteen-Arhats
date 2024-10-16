package org.lkg.utils;

import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/15 1:49 PM
 */
public class HttpServletUtil {


    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtils.isEmpty(requestAttributes)) {
            return null;
        }
        return (HttpServletRequest)
                requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
    }

}
