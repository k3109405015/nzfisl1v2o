package com.kun.tools;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

/**
 * 身份证号解析工具类，支持提取出生日期、性别与年龄。
 */
public class IdCardUtil {

    private static final int GENDER_INDEX = 16;

    /**
     * 从18位身份证号提取出生日期
     *
     * <p>规则：
     * - 第7~14位为出生日期（yyyyMMdd）
     * - 自动转换为 LocalDate
     *
     * @param idCard 18位身份证号
     * @return 出生日期 LocalDate
     */
    public static LocalDate parseBirthDate(String idCard) {
        String birthday = parseBirthDateString(idCard);

        try {
            return LocalDateUtil.parseToDate(birthday);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("身份证出生日期不合法: " + birthday, e);
        }
    }

    /**
     * 从18位身份证号提取出生日期
     * - 第7~14位为出生日期（yyyyMMdd）
     *
     * @param idCard 18位身份证号
     * @return 出生日期字符串（yyyyMMdd 格式）
     */
    public static String parseBirthDateString(String idCard) {
        AssertUtil.notNull(idCard, "idCard must not be empty");
        AssertUtil.isTrue(idCard.length() == 18, "身份证号必须为18位");
        return idCard.substring(6, 14);
    }

    /**
     * 获取性别
     *
     * @param idCard 18位身份证号
     * @return 1：男，0：女
     */
    public static int getGender(String idCard) {
        AssertUtil.notNull(idCard, "idCard must not be empty");
        AssertUtil.isTrue(idCard.length() == 18, "身份证号必须为18位");

        char genderChar = idCard.charAt(GENDER_INDEX);

        if (!Character.isDigit(genderChar)) {
            throw new IllegalArgumentException("身份证性别位非法: " + genderChar);
        }

        int genderCode = genderChar - '0';

        return (genderCode % 2 == 0) ? 1 : 0;
    }

    /**
     * 根据身份证号计算年龄
     *
     * @param idCard 18位身份证号
     * @return 年龄（周岁）
     */
    public static int getAge(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            throw new IllegalArgumentException("身份证号必须为18位");
        }
        LocalDate localDate = parseBirthDate(idCard);
        return Period.between(localDate, LocalDate.now()).getYears();
    }

}
