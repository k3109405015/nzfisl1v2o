package com.kun.tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {

    public static final DateTimeFormatter DATETIME_SECOND_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 工具类，禁止实例化。 */
    private LocalDateTimeUtil() {
    }

    /**
     * 获取指定时间的默认格式（yyyyMMddHHmmss）字符串
     *
     * @param dateTime 时间对象
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DATETIME_SECOND_FORMATTER);
    }

    /**
     * 获取指定时间的自定义格式字符串
     *
     * @param dateTime 时间对象
     * @param pattern  自定义格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, DateTimeFormatter pattern) {
        AssertUtil.notNull("datetime and pattern must not be null", dateTime, pattern);
        return dateTime.format(pattern);
    }

    /**
     * 获取当前时间的默认格式字符串（yyyyMMddHHmmss）
     *
     * @return 格式化后的字符串
     */
    public static String now() {
        return format(LocalDateTime.now());
    }

    /**
     * 获取当前时间的自定义格式字符串
     *
     * @param pattern 自定义格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的字符串
     */
    public static String now(DateTimeFormatter pattern) {
        AssertUtil.notNull(pattern, "pattern must not be empty");
        return format(LocalDateTime.now(), pattern);
    }

}
