package org.lkg.metric.sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/14 2:12 PM
 */
public class FuzzySqlUtil {

    static class ReplaceColumnAndLongValues extends ExpressionDeParser {

        @Override
        public void visit(StringValue stringValue) {
            this.getBuffer().append("?");
        }

        @Override
        public void visit(LongValue longValue) {
            this.getBuffer().append("?");
        }
    }

    public static String cleanStatement(String sql) throws JSQLParserException {
        StringBuilder buffer = new StringBuilder();
        ExpressionDeParser expr = new ReplaceColumnAndLongValues();

        SelectDeParser selectDeparser = new SelectDeParser(expr, buffer);
        expr.setSelectVisitor(selectDeparser);
        expr.setBuffer(buffer);
        StatementDeParser stmtDeParser = new StatementDeParser(expr, selectDeparser, buffer);

        Statement stmt = CCJSqlParserUtil.parse(sql);
        stmt.accept(stmtDeParser);

        return stmtDeParser.getBuffer().toString();
    }

    public static String onlyFromSql(String s) {
        return s.substring(s.indexOf("FROM"));
    }


    public static void main(String[] args) {

        try {
            System.out.println(onlyFromSql("select user,name from user_info where user_id=2 and user_name in('2','5')"));
            System.out.println(cleanStatement("select * from user_info where user_id='2920-10-01' or user_tim >= 20230101"));

        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }
}
