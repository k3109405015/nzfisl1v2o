package com.kun.tools;

import java.util.AbstractMap;
import java.util.regex.Pattern;

/**
 * 字符串键值对解析工具类。
 */
public class StringPairUtil {

    /** 工具类，禁止实例化。 */
    private StringPairUtil() {
    }

    /**
     * 将 key=value 格式的字符串解析为键值对。
     * <br/> 示例：
     * "name=Tom" -> key: name, value: Tom "age=18"   -> key: age, value: 18
     *
     * @param input     输入字符串，格式为 key=value
     * @param delimiter 分隔符，默认 "="
     * @return 返回键值对（SimpleEntry）
     * @throws IllegalArgumentException 如果格式不合法
     */
    public static AbstractMap.SimpleEntry<String, String> toEntry(String input, String delimiter) {
        AssertUtil.notNull("Input and Delimiter string must not be null or blank", input, delimiter);
        // 只分割一次，避免 value 中包含分隔符导致错误
        String[] parts = input.split(Pattern.quote(delimiter), 2);
        AssertUtil.isTrue(parts.length == 2,
                "Invalid format. Expected 'key" + delimiter + "value', but got: " + input);
        return new AbstractMap.SimpleEntry<>(parts[0].trim(), parts[1].trim());
    }

    /**
     * 将 key=value 格式的字符串解析为键值对，默认分隔符为 {@code =}。
     *
     * @param input 输入字符串
     * @return 键值对
     */
    public static AbstractMap.SimpleEntry<String, String> toEntry(String input) {
        return toEntry(input, "=");
    }

}
