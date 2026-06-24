package com.clubmanage.common;

import java.time.format.DateTimeFormatter;
import com.clubmanage.common.TimeUtil;
import java.util.Locale;

/**
 * 缁熶竴鐨勬棩鏈熸椂闂村瓧绗︿覆鐢熸垚鍣ㄣ€? * <p>
 * 涓轰簡褰诲簳閬垮厤 SQLite / MySQL JDBC 椹卞姩瀵?DATETIME 鍒楄皟鐢?getTimestamp()
 * 鏃跺嚭鐜?"Error parsing time stamp" 绛夎В鏋愬紓甯革紝鏈」鐩皢鎵€鏈夋椂闂村垪浠?TEXT
 * 鏂瑰紡璇诲啓锛孞ava 瀹炰綋瀛楁绫诲瀷鐢?String锛岃€岄潪 LocalDateTime銆? * <p>
 * 鏁版嵁搴撶粺涓€瀛樺偍鏍煎紡锛歽yyy-MM-dd HH:mm:ss
 */
public final class TimeUtil {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    private TimeUtil() {}

    public static String now() {
        return TimeUtil.now().format(FMT);
    }

    public static String format(LocalDateTime dt) {
        return dt == null ? null : dt.format(FMT);
    }

    public static LocalDateTime parse(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(s.trim(), FMT);
    }
}
