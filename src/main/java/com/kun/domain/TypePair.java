package com.kun.domain;

import lombok.Data;

@Data
public final class TypePair {

    private final Class<?> sourceType;
    private final Class<?> targetType;

    public TypePair(Class<?> sourceType, Class<?> targetType) {
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

}