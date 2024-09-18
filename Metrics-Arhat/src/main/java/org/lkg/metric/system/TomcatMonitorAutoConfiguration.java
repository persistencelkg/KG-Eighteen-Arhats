package org.lkg.metric.system;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.tomcat.TomcatMetrics;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.lkg.core.config.EnableLongHengMetric;
import org.lkg.core.init.LongHengMeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableLongHengMetric
@ConditionalOnClass(Tomcat.class)
@ConditionalOnWebApplication
public class TomcatMonitorAutoConfiguration implements ApplicationListener<ServletWebServerInitializedEvent>{

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        WebServer webServer = event.getWebServer();
        if (!(webServer instanceof TomcatWebServer)){
            return;
        }

        for (Container container : ((TomcatWebServer)webServer).getTomcat().getHost().findChildren()){
            if (container instanceof Context){
                new TomcatMetrics(((Context) container).getManager(), Tags.empty()).bindTo(LongHengMeterRegistry.getInstance());
            }
        }
    }
}
