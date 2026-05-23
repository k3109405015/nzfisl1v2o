package com.kun.tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {

    // 默认时间格式 yyyyMMddHHmmss
    private static final String DEFAULT_PATTERN = "yyyyMMddHHmmss";

    private LocalDateTimeUtil() {
    }

    /**
     * 获取指定时间的默认格式（yyyyMMddHHmmss）字符串
     *
     * @param dateTime 时间对象
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_PATTERN);
    }

    /**
     * 获取指定时间的自定义格式字符串
     *
     * @param dateTime 时间对象
     * @param pattern  自定义格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        AssertUtil.notNull("datetime and pattern must not be null", dateTime, pattern);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * 获取当前时间的默认格式字符串（yyyyMMddHHmmss）
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
    public static String now(String pattern) {
        AssertUtil.notNull(pattern, "pattern must not be empty");
        return format(LocalDateTime.now(), pattern);
    }

}
