package com.kun.service;

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