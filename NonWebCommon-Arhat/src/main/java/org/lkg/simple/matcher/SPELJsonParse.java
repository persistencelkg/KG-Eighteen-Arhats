package org.lkg.simple.matcher;

import lombok.extern.slf4j.Slf4j;
import org.lkg.simple.JacksonUtil;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/4/1 3:01 PM
 */
@Slf4j
public class SPELJsonParse {
    private static final ExpressionParser parser = new SpelExpressionParser();

    public static Map<String, Object> extraFeature(Map<String, Object> data, @NotNull String prefix, @NotNull Map<String, String> aliasMap) {
        if (ObjectUtils.isEmpty(data)) {
            return null;
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable(prefix, data);
        Map<String, Object> map = new HashMap<>();
        aliasMap.forEach((k, v) -> {
            String key = aliasMap.get(k);
            // 如果已经有了直接处理
            if (data.containsKey(key)) {
                map.put(key, data.get(key));
                return;
            }
            String format = MessageFormat.format("#{0}{1}", prefix, k);
            try {
                map.put(key, parser.parseExpression(format).getValue(context));
            } catch (Exception e) {
                log.warn("data:{} spel parse key:{} fail, reason:{}", data, k, e.getMessage());
            }
        });
        return map;

    }

    public static void main(String[] args) {
        String json = "{ \"name\": \"张三\", \"age\": 30, \"address\": [{ \"city\": \"北京\", \"street\": \"长安街\" },{ \"city\": \"西宁\", \"street\": \"长2安街\" }] }";

        Map<String, Object> stringObjectMap = JacksonUtil.readMap(json);
        HashMap<String, String> alais = new HashMap<>();
        alais.put("[name]", "user_name");
        alais.put("[address] [1][city]", "loc");
        System.out.println(extraFeature(stringObjectMap, "user", alais));
    }
}
