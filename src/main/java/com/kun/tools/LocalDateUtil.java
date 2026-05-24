package com.kun.tools;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * 解析字符串为日期（只保留日期，不含时间）
     * 支持：
     * - yyyyMMdd
     * - yyyy-MM-dd
     *
     * @return LocalDate（时间部分会被丢弃）
     */
    public static LocalDate parseToDate(String text) {
        AssertUtil.notNull(text, "text must not be null");

        try {
            if (text.contains("-")) {
                return LocalDate.parse(text, DATE_TIME_FORMATTER);
            }

            if (text.length() == 8 && !text.contains("-")) {
                return LocalDate.parse(text, DATE_FORMATTER);
            }

            throw new IllegalArgumentException("Unsupported date format: " + text);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Parse date failed: " + text, e);
        }
    }

}
