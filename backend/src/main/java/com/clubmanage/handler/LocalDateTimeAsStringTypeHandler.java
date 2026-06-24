package com.clubmanage.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 鎶婃暟鎹簱涓殑 DATETIME / TIMESTAMP 鍒?*浠ュ瓧绗︿覆鏂瑰紡**璇诲彇骞惰В鏋愪负 LocalDateTime銆? *
 * 瑙ｅ喅 SQLite JDBC 椹卞姩锛堝挨鍏舵槸鑰佺増鏈湪鏌愪簺鎯呭喌涓嬭皟鐢?rs.getTimestamp() 浼氭姏鍑? * "Error parsing time stamp"銆傝€屾櫘閫氬瓧绗︿覆鏂瑰紡璇诲彇骞惰В鏋愬彲浠ュ畬鍏ㄩ伩鍏嶈繖涓棶棰樸€? *
 * 鍐欏叆鏃朵粛鐒舵寜鏍囧噯鏍煎紡 yyyy-MM-dd HH:mm:ss 鎴栨暟鎹簱椹卞姩鎻愪緵鏃ユ湡鏃堕棿銆? */
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
        // 鏈€鍚庝竴娆＄敤 ISO_LOCAL_DATE_TIME;
        try {
            return LocalDateTime.parse(s);
        } catch (Exception e) {
            return null;
        }
    }
}
