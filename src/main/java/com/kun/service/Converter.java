package com.kun.service;

/**
 * 类型转换函数式接口。
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 */
@FunctionalInterface
public interface Converter<S, T> {

    /**
     * 将源类型转换为目标类型。
     *
     * @param source 源对象
     * @return 转换后的目标对象
     */
    T convert(S source);
}