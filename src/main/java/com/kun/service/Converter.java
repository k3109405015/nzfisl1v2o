package com.kun.service;

@FunctionalInterface
public interface Converter<S, T> {

    T convert(S source);
}