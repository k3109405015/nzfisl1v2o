package com.kun.tools;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TypeConvertUtil {

    /**
     * 内置基础类型转换器
     */
    private static final Map<Class<?>, Function<String, Object>> CONVERTERS = new HashMap<>();

    static {
        // String
        CONVERTERS.put(String.class, v -> v);

        // int / Integer
        CONVERTERS.put(int.class, Integer::parseInt);
        CONVERTERS.put(Integer.class, Integer::parseInt);

        // long / Long
        CONVERTERS.put(long.class, Long::parseLong);
        CONVERTERS.put(Long.class, Long::parseLong);

        // double / Double
        CONVERTERS.put(double.class, Double::parseDouble);
        CONVERTERS.put(Double.class, Double::parseDouble);

        // float / Float
        CONVERTERS.put(float.class, Float::parseFloat);
        CONVERTERS.put(Float.class, Float::parseFloat);

        // boolean / Boolean
        CONVERTERS.put(boolean.class, TypeConvertUtil::toBoolean);
        CONVERTERS.put(Boolean.class, TypeConvertUtil::toBoolean);

        // BigDecimal（常用保留）
        CONVERTERS.put(BigDecimal.class, BigDecimal::new);
    }

    /**
     * 主转换方法：String -> 目标类型
     */
    public static Object convert(String value, Class<?> targetType) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        value = value.trim();

        Function<String, Object> converter = CONVERTERS.get(targetType);
        if (converter != null) {
            return converter.apply(value);
        }

        throw new IllegalArgumentException("Unsupported type: " + targetType.getName());
    }

    /**
     * boolean 增强解析（兼容 1/0 / true/false）
     */
    private static Boolean toBoolean(String value) {
        if (value == null) return false;

        String v = value.trim().toLowerCase();

        // true 语义
        if ("true".equals(v) || "1".equals(v) || "yes".equals(v) || "y".equals(v)) {
            return true;
        }

        // false 语义
        if ("false".equals(v) || "0".equals(v) || "no".equals(v) || "n".equals(v)) {
            return false;
        }

        throw new IllegalArgumentException("Invalid boolean value: " + value);
    }

    /**
     * 扩展点：允许外部注册转换器
     */
    public static void register(Class<?> type, Function<String, Object> converter) {
        CONVERTERS.put(type, converter);
    }
}