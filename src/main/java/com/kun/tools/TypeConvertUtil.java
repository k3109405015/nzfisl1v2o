package com.kun.tools;

import com.kun.domain.TypePair;
import com.kun.service.Converter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型转换工具类，支持注册自定义 {@link Converter} 并进行类型转换。
 */
public class TypeConvertUtil {

    /**
     * 转换器缓存
     */
    private static final Map<TypePair, Converter<?, ?>> CONVERTERS =
            new ConcurrentHashMap<>();

    private static final Set<String> TRUE_VALUES =
            Set.of("true", "1", "yes", "y", "on");

    private static final Set<String> FALSE_VALUES =
            Set.of("false", "0", "no", "n", "off");


    static {
        /*
         * String -> Number
         */
        register(String.class, Integer.class, Integer::parseInt);
        register(String.class, Long.class, Long::parseLong);
        register(String.class, Double.class, Double::parseDouble);
        register(String.class, Float.class, Float::parseFloat);
        register(String.class, Short.class, Short::parseShort);
        register(String.class, Byte.class, Byte::parseByte);

        /*
         * String -> Boolean
         */
        register(String.class, Boolean.class, source -> {
            String v = source.trim().toLowerCase();
            if (TRUE_VALUES.contains(v)) {
                return true;
            }
            if (FALSE_VALUES.contains(v)) {
                return false;
            }
            throw new IllegalArgumentException(
                    "Invalid boolean value: " + source
            );
        });

        /*
         * String -> BigDecimal
         */
        register(String.class, BigDecimal.class, BigDecimal::new);

    }

    /**
     * 类型转换
     *
     * @param source     源对象
     * @param targetType 目标类型
     * @param <T>        目标类型泛型
     * @return 转换后的对象；source 为 null 时返回 null
     * @throws IllegalArgumentException 找不到对应转换器时抛出
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object source, Class<T> targetType) {

        if (source == null) {
            return null;
        }
        AssertUtil.notNull(source, "source must not be null");

        Class<?> sourceType = source.getClass();

        Converter<Object, T> converter =
                (Converter<Object, T>) CONVERTERS.get(
                        new TypePair(sourceType, targetType)
                );

        if (converter == null) {
            /*
             * 已经是目标类型
             */
            if (targetType.isAssignableFrom(sourceType)) {
                return (T) source;
            }
            throw new IllegalArgumentException(
                    "No converter found from [%s] to [%s]"
                            .formatted(
                                    sourceType.getName(),
                                    targetType.getName()
                            )
            );
        }
        return converter.convert(source);
    }


    /**
     * 注册转换器
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @param converter  转换函数
     * @param <S>          源类型泛型
     * @param <T>          目标类型泛型
     */
    public static <S, T> void register(Class<S> sourceType, Class<T> targetType,
                                       Converter<S, T> converter) {

        CONVERTERS.put(new TypePair(sourceType, targetType), converter);

    }

}