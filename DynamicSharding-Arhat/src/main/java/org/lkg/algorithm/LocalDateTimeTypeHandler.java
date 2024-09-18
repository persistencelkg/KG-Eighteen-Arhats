package org.lkg.algorithm;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.lkg.simple.DateTimeUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * sharding-jdbc没有实现这个LocalDateTime的转换，这里自定义转换类
 * ps：typeHandler底层是通过反射递归找子类实例，因此不需要注入，或者说明确指定类型转换器，自动识别的
 */
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i,parameter);
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return DateTimeUtils.convertToLocalDateTime(rs.getDate(columnName));
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return DateTimeUtils.convertToLocalDateTime(rs.getDate(columnIndex));
    }

    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return  DateTimeUtils.convertToLocalDateTime(cs.getDate(columnIndex));

    }
}
