package org.lkg.core.expression;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.*;
import org.apache.commons.jexl3.introspection.JexlUberspect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/15 5:20 PM
 */
@Slf4j
public class JexlExpressionManger {
    private JexlExpression expression;

    private final JexlEngine engine;

    private static JexlExpressionManger INSTANCE = null;

    public static JexlExpressionManger getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new JexlExpressionManger();
        }
        return INSTANCE;
    }

    private JexlExpressionManger() {
        JexlBuilder builder = new JexlBuilder();
        builder.arithmetic(new LongAdaptJexlArithmetic(false));
        engine = builder.create();
    }


    public void setExpression(String expression) {
        try {
            this.expression = engine.createExpression(expression);
        } catch (Exception e) {
            log.error("parse expression failed,expression:{}", expression, e);
        }

    }

    public String getExpression() {
        return expression == null ? null : expression.getSourceText();
    }

    public boolean match(Map<String, Object> args) {
        JexlExpression expression = this.expression;
        try {
            JexlContext context = new MapContext(args);
            return expression != null && (boolean) expression.evaluate(context);
        } catch (Exception e) {
            log.error("parse condition expression [{}] failed", expression == null ? null : expression.getSourceText(), e);
            return false;
        }
    }

    public static void main(String[] args) {
        JexlExpressionManger jexlExpressionManger = new JexlExpressionManger();
        jexlExpressionManger.setExpression("(city_id =~ [3,45]) && (count > 4)");
        HashMap<String, Object> map = new HashMap<>();
        long city = 3;
        map.put("city_id", city);
        map.put("count", 5);
        map.put("test", "3");
        System.out.println(jexlExpressionManger.match(map));

        System.out.println(jexlExpressionManger.getExpression());

    }
}
