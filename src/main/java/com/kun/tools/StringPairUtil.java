package com.kun.tools;

import org.springframework.util.Assert;

import java.util.AbstractMap;
import java.util.regex.Pattern;

public class StringPairUtil {

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
        Assert.hasLength(input, "Input string must not be null or blank");
        Assert.hasLength(delimiter, "Delimiter string must not be null or blank");
        // 只分割一次，避免 value 中包含分隔符导致错误
        String[] parts = input.split(Pattern.quote(delimiter), 2);
        Assert.isTrue(parts.length == 2,
                "Invalid format. Expected 'key" + delimiter + "value', but got: " + input);
        return new AbstractMap.SimpleEntry<>(parts[0].trim(), parts[1].trim());
    }

    public static AbstractMap.SimpleEntry<String, String> toEntry(String input) {
        return toEntry(input, "=");
    }

}
