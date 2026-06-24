package com.clubmanage.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 把数据库中的 DATETIME / TIMESTAMP 列**以字符串方式**读取并解析为 LocalDateTime。
 *
 * 解决 SQLite JDBC 驱动（尤其是老版本在某些情况下调用 rs.getTimestamp() 会抛出
 * "Error parsing time stamp"。而普通字符串方式读取并解析可以完全避免这个问题。
 *
 * 写入时仍然按标准格式 yyyy-MM-dd HH:mm:ss 或数据库驱动提供日期时间。
 */
public class LocalDateTimeAsStringTypeHandler extends BaseTypeHandler<LocalDateTime> {

    private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    private static final List<DateTimeFormatter> TRY_PATTERNS = Arrays.asList(
            OUT_FMT,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ROOT),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.ROOT),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.ROOT)
    );

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(OUT_FMT));
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String s = rs.getString(columnName);
        return parse(s);
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String s = rs.getString(columnIndex);
        return parse(s);
    }

    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String s = cs.getString(columnIndex);
        return parse(s);
    }

    private static LocalDateTime parse(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        for (DateTimeFormatter fmt : TRY_PATTERNS) {
            try {
                return LocalDateTime.parse(s, fmt);
            } catch (Exception ignored) {
                // try next
            }
        }
        // 最后一次用 ISO_LOCAL_DATE_TIME;
        try {
            return LocalDateTime.parse(s);
        } catch (Exception e) {
            return null;
        }
    }
}
