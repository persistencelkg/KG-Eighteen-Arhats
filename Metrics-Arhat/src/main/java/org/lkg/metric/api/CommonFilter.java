package org.lkg.metric.api;

import org.springframework.core.Ordered;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/5 4:53 PM
 */
public interface CommonFilter extends Ordered {

    void filter(SelfChain selfChain);

    interface SelfChain {

        HttpServletRequest request();

        HttpServletResponse response();

        void proceed() throws ServletException, IOException;

        Duration duration();

    }
}
