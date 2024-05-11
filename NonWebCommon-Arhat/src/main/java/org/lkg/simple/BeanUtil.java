package org.lkg.simple;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/** @author zhoujiaqi1 on 2021/4/27 8:30 下午 */
@Component
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        if (Objects.isNull(applicationContext)){
            applicationContext = arg0;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getObject(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> Collection<T> listBean(Class<T> tClass) {
        Map<String, T> beansOfType = applicationContext.getBeansOfType(tClass);
        if (ObjectUtil.isEmpty(beansOfType)) {
            return null;
        }
        return beansOfType.values();
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
