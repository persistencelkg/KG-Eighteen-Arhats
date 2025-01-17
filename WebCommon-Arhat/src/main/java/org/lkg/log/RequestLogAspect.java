package org.lkg.log;

import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.lkg.utils.JacksonUtil;
import org.lkg.utils.ServerInfo;
import org.lkg.utils.HttpServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/15 11:05 AM
 */
@Aspect
@Component
public class RequestLogAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestLogAspect.class.getSimpleName());
    private static final ThreadLocal<LogTarget> LOG_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<LogTarget> FEIGN_LOG_LOCAL = new ThreadLocal<>();
    private static final List<String> IGNORED_HEADER_KEY = new ArrayList<String>() {{
        add("Content-Length");
        add("Content-Type");
        add("cookie");
        add("user-agent");
        add("cache-control");
        add("connection");
        add("host");
        add("upgrade-insecure-requests");
    }};
    // sec* and accept* use pattern

    public static LogTarget getFeignLogTarget() {
        return FEIGN_LOG_LOCAL.get();
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)" +
            "|| @within(org.springframework.stereotype.Controller)")
    // TODO 引入dubbo相关接口
    public void pointCut() {

    }

    @Pointcut("@within(org.springframework.cloud.openfeign.FeignClient)")
    public void feignPointCut() {

    }

    @Pointcut("@within(org.lkg.log.LogIgnore)")
    public void ignore() {

    }


    @Around("feignPointCut() && !ignore()")
    public Object aroundForFeign(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (Objects.isNull(FEIGN_LOG_LOCAL.get())) {
            FEIGN_LOG_LOCAL.set(new LogTarget("Feign "));
        }
        return aroundForRequest(proceedingJoinPoint, FEIGN_LOG_LOCAL);
    }

    @Around("pointCut() && !ignore()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        LogTarget logTarget = new LogTarget("Controller ");
        LOG_LOCAL.set(logTarget);
        return aroundForRequest(proceedingJoinPoint, LOG_LOCAL);
    }

    private Object aroundForRequest(ProceedingJoinPoint proceedingJoinPoint, ThreadLocal<LogTarget> threadLocal) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        Object[] args = proceedingJoinPoint.getArgs();
        long l = System.nanoTime();
        boolean suc = true;
        try {
            LogTarget logTarget = threadLocal.get();
            // arg method
            processArgsMethod(logTarget, signature, args);
            HttpServletRequest request = HttpServletUtil.getRequest();
            if (!ObjectUtils.isEmpty(request)) {
                logTarget.setIpHost(ServerInfo.ipPort());
                logTarget.setUrl(request.getRequestURI());
                Enumeration<String> headerNames = request.getHeaderNames();
                HashMap<String, Object> headMap = new HashMap<>();
                while (headerNames.hasMoreElements()) {
                    String s = headerNames.nextElement();
                    if (IGNORED_HEADER_KEY.contains(s) || s.startsWith("sec") || s.startsWith("accept")) {
                        continue;
                    }
                    headMap.put(s, request.getHeader(s));
                }
                logTarget.setHeader(headMap);
            }
            // else in feignInterceptor
            threadLocal.set(logTarget);
            Object proceed = proceedingJoinPoint.proceed();
            logTarget.setResult(proceed);
            return proceed;
        } catch (Throwable e) {
            suc = false;
            throw e;
        } finally {
            LogTarget logTarget = threadLocal.get();
            // 异常经过的时间也记录下
            logTarget.setCost(Duration.ofNanos(System.nanoTime() - l).toMillis());
            logTarget.setPrefix(logTarget.getPrefix() +(suc ? "success" : "fail"));
            log.info(LogTarget.buildMsg(logTarget));
            // 异常由GlobalExceptionHandler负责统一处理, 通过trace_id 锁定即可无需过多打印
            threadLocal.remove();
        }
    }

    private void processArgsMethod(LogTarget logTarget, Signature signature, Object[] args) {
        ArrayList<Object> list = new ArrayList<>();
        MethodSignature methodSignature = (MethodSignature) signature;
        logTarget.setClassMethodName(signature.getDeclaringType().getSimpleName() + "#" + signature.getName());
        Annotation[][] annotations = methodSignature.getMethod().getParameterAnnotations();
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        try {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                // 元数据 文件类 无需收集
                if (arg instanceof ServletRequest
                        || arg instanceof ServletResponse
                        || arg instanceof MultipartFile) {
                    continue;
                }
                Annotation[] arr = annotations[i];
                boolean hasProcessPath = false;
                for (Annotation annotation : arr) {
                    // 参数注解排除，敏感信息过滤
                    if (Objects.equals(LogIgnore.class, annotation.annotationType())) {
                        list.add("**sensitive**");
                        break;
                    }
                    if (annotation instanceof PathVariable) {
                        String value = ((PathVariable) annotation).value();
                        if (value.isEmpty()) {
                            value = parameters[i].getName();
                        }
                        Map<String, Object> paramMap = Collections.singletonMap(value, arg);
                        list.add(paramMap);
                        hasProcessPath = true;
                    }
                }
                if (!hasProcessPath) {
                    list.add(arg);
                }
            }
            logTarget.setArgs(list);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

    }


    @Data
    static class LogTarget {

        private long cost;
        // class + method
        private String ipHost;
        private String url;
        private String classMethodName;
        private List<Object> args;
        private Object result;
        private Map<String, Object> header;
        private String prefix;

        private LogTarget(String prefix) {
            this.prefix = prefix;
        }


        private static final String MSG_TEMPLATE = "[{7}]: resp data:{4} consume:{5} ms, url:{0}{1}, method:{2}, args:{3} header:{6}";

        private static final Function<LogTarget, String> MSG_SUPPLY = (logTarget) ->
                MessageFormat.format(MSG_TEMPLATE,
                        logTarget.getIpHost(),
                        logTarget.getUrl(),
                        logTarget.getClassMethodName(),
                        JacksonUtil.writeValue(logTarget.getArgs()),
                        JacksonUtil.writeValue(logTarget.getResult()),
                        String.valueOf(logTarget.getCost()),
                        logTarget.getHeader(),
                        logTarget.getPrefix());

        public static String buildMsg(LogTarget logTarget) {
//            if (Objects.isNull(logTarget)) {
//                return "loss";
//            }
            return MSG_SUPPLY.apply(logTarget);
        }
    }

}
