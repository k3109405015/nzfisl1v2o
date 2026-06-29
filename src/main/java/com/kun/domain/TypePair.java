package com.kun.domain;

import lombok.Data;

@Data
public final class TypePair {

    private final Class<?> sourceType;
    private final Class<?> targetType;

    /**
     * 构造类型对，用于标识源类型与目标类型的转换关系。
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     */
    public TypePair(Class<?> sourceType, Class<?> targetType) {
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

}