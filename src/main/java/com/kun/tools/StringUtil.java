package com.kun.tools;

import java.util.Map;

/**
 * 字符串处理工具类。
 */
public class StringUtil {

    /** 工具类，禁止实例化。 */
    private StringUtil() {
    }

    /**
     * 将字符串首字母大写。
     *
     * @param name 原始字符串
     * @return 首字母大写后的字符串
     */
    public static String capitalize(String name) {
        AssertUtil.notNull(name, "name must not be null or blank");
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * 判断第一个字符是否为字母
     *
     * @param str 待判断字符串
     * @return 首字符为字母时返回 true，空字符串返回 false
     */
    public static boolean isFirstCharLetter(String str) {
        return !ObjectUtil.isEmpty(str) && Character.isLetter(str.charAt(0));
    }

    /**
     * 将参数 Map 拼接为 GET 请求 URL。
     *
     * @param params 请求参数
     * @return 拼接后的 URL
     */
    public static String buildGetUrl(Map<String, Object> params) {
        if (ObjectUtil.isEmpty(params)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (ObjectUtil.isEmpty(value)) {
                continue;
            }
            if (!first) {
                builder.append("&");
            }
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(value);
            first = false;
        }
        return builder.toString();
    }

    /**
     * 将 Java Bean 的简单类型字段拼接为 GET 请求参数字符串。
     *
     * @param object 源对象
     * @return 拼接后的 URL 参数字符串
     */
    public static String buildGetUrl(Object object) {
        Map<String, Object> map = BeanUtil.toMap(object);
        if (ObjectUtil.isEmpty(map)) {
            return "";
        }
        return buildGetUrl(map);
    }

}
