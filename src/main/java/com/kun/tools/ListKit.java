package com.kun.tools;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListKit {

    /**
     * List 对象转换（带单个增强逻辑）
     *
     * @param rows     源数据
     * @param clazz    目标类型
     * @param consumer 可选增强逻辑（row -> entity）
     */
    public static <R, E> List<E> convertList(List<R> rows, Class<E> clazz, BiConsumer<R, E> consumer) {

        AssertUtil.notNull(consumer, "Consumer cannot be null");
        if (ObjectUtil.isEmpty(rows)) {
            return List.of();
        }

        return rows.stream().map(row -> {
            E entity = BeanUtil.copy(row, clazz);
            consumer.accept(row, entity);
            return entity;
        }).toList();
    }

    /**
     * List 对象转换（无增强逻辑）
     */
    public static <R, E> List<E> convertList(List<R> rows, Class<E> clazz) {

        if (ObjectUtil.isEmpty(rows)) {
            return List.of();
        }

        return rows.stream().map(row -> BeanUtil.copy(row, clazz)).toList();
    }

    /**
     * 按 key 校验 + 合并（b 覆盖 a）
     * <p>
     * 使用场景：
     * - a 是基础数据
     * - b 是更新数据
     * - key 相同则 b 覆盖 a
     */
    public static <T, K> List<T> mergeOverrideByKey(
            List<T> base,
            List<T> override,
            Function<T, K> keyExtractor) {

        if (ObjectUtil.isEmpty(base) || ObjectUtil.isEmpty(override)) {
            return List.of();
        }

        // a 的 key 集合
        Set<K> aKeys = base.stream().map(keyExtractor).collect(Collectors.toSet());

        // 找非法元素
        boolean b1 = override.stream().allMatch(e -> aKeys.contains(keyExtractor.apply(e)));
        AssertUtil.isTrue(b1, "Some elements in set b do not exist in set a");

        Map<K, T> map = base.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
        // 再放 b（覆盖 a）
        for (T item : override) {
            map.put(keyExtractor.apply(item), item);
        }

        return new ArrayList<>(map.values());
    }

    /**
     * 合并
     */
    public static <T, K> List<T> mergeWithOverrideByKey(List<T> a, List<T> b, Function<T, K> keyExtractor) {
        AssertUtil.notNull(a, "a list must not be null");
        if (ObjectUtil.isEmpty(b)) {
            return a;
        }

        Map<K, T> map = a.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
        // 再放 b（覆盖 a）
        for (T item : b) {
            map.put(keyExtractor.apply(item), item);
        }

        return new ArrayList<>(map.values());
    }


    /**
     * 多字段去重（按 key 组合）
     */
    @SafeVarargs
    public static <T> List<T> distinctByKeys(List<T> list, Function<T, ?>... keyExtractors) {
        AssertUtil.notNull(keyExtractors, "keyExtractors must not be null");
        if (ObjectUtil.isEmpty(list)) {
            return List.of();
        }

        return new ArrayList<>(list.stream().collect(Collectors.toMap(item -> buildKey(item, keyExtractors),
                Function.identity(), (a, b) -> a)).values());
    }

    /**
     * 通用 groupBy，参数1 是list，参数2后面的，是方法引用，去重的Key
     */
    @SafeVarargs
    public static <T> Map<String, List<T>> groupBy(List<T> list, Function<T, ?>... keyExtractors) {
        AssertUtil.notNull(keyExtractors, "keyExtractors must not be null");
        if (ObjectUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }

        return list.stream().collect(Collectors.groupingBy(item -> buildKey(item, keyExtractors)));
    }

    /**
     * 按 key 对数据进行分组并合并，每个 key 生成一个主对象，并回填其子集合数据。
     *
     * <p>处理流程：</p>
     * <p>1. 遍历 Map，获取每个 key 对应的 value 集合</p>
     * <p>2. 对 value 集合进行去重，生成唯一的主对象</p>
     * <p>3. 根据规则将子对象集合回填到主对象中（Set 类型）</p>
     *
     * <p>注意事项：</p>
     * <p>- 主对象类型由参数 3 指定</p>
     * <p>- 子集合使用 Set 存储，保证去重</p>
     */
    public static <T, S> List<T> mergeGroup(Map<String, List<T>> groupMap, Class<S> subClass,
                                            Predicate<T> filter,
                                            BiConsumer<T, Set<S>> subSetter) {

        if (ObjectUtil.isEmpty(groupMap)) {
            return List.of();
        }

        return groupMap.values().stream().map(group -> {
            // 主对象
            T main = group.get(0);
            // 聚合子对象
            Set<S> subSet = group.stream().filter(filter).map(item -> BeanUtil.copy(item, subClass))
                    .collect(Collectors.toSet());
            // 回填
            subSetter.accept(main, subSet);
            return main;
        }).toList();

    }

    @SafeVarargs
    private static <T> String buildKey(T obj, Function<T, ?>... keyExtractors) {
        StringBuilder sb = new StringBuilder();
        for (Function<T, ?> extractor : keyExtractors) {
            Object val = extractor.apply(obj);
            sb.append(val).append("#");
        }
        return sb.toString();
    }

}