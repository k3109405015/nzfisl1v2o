package com.kun.tools;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * List 集合操作工具类，提供转换、合并、分组、去重等常用能力。
 *
 * @author GaoYu
 */
public class ListKit {

    /**
     * List 对象转换（带单个增强逻辑）
     *
     * <p>先将源元素浅拷贝为目标类型，再执行 consumer 进行额外赋值或增强。</p>
     *
     * @param rows     源数据
     * @param clazz    目标类型
     * @param consumer 增强逻辑（row -> entity）
     * @param <R>        源元素类型
     * @param <E>        目标元素类型
     * @return 转换后的列表；rows 为空时返回空列表
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
     *
     * <p>通过 {@link BeanUtil#copy(Object, Class)} 将源列表元素浅拷贝为目标类型。</p>
     *
     * @param rows  源数据
     * @param clazz 目标类型
     * @param <R>   源元素类型
     * @param <E>   目标元素类型
     * @return 转换后的列表；rows 为空时返回空列表
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
     *
     * <p>要求 override 中每个元素的 key 必须存在于 base 中，否则抛出异常。</p>
     *
     * @param base         基础列表
     * @param override     覆盖列表
     * @param keyExtractor key 提取函数
     * @param <T>            元素类型
     * @param <K>            key 类型
     * @return 合并后的列表；base 或 override 为空时返回空列表
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
     * 按 key 合并两个列表，b 覆盖 a 中同 key 元素。
     *
     * <p>与 {@link #mergeOverrideByKey(List, List, Function)} 不同，本方法不校验 b 的 key 是否存在于 a。</p>
     *
     * @param a            基础列表（不可为 null）
     * @param b            覆盖列表
     * @param keyExtractor key 提取函数
     * @param <T>            元素类型
     * @param <K>            key 类型
     * @return 合并后的列表；b 为空时直接返回 a
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
     *
     * <p>将多个字段值拼接为组合 key，保留首次出现的元素。</p>
     *
     * @param list           源列表
     * @param keyExtractors  字段提取函数（可变参数）
     * @param <T>              元素类型
     * @return 去重后的列表；list 为空时返回空列表
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
     * 按多字段组合 key 对列表分组。
     *
     * <p>将多个字段值拼接为组合 key（{@code val1#val2#...}），相同 key 的元素归入同一组。</p>
     *
     * @param list           源列表
     * @param keyExtractors  字段提取函数（可变参数）
     * @param <T>              元素类型
     * @return 分组 Map；list 为空时返回空 Map
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
     *
     * @param groupMap  分组 Map（key 为分组标识，value 为同组元素列表）
     * @param subClass  子对象类型
     * @param filter    子对象过滤条件
     * @param subSetter 子集合回填函数（主对象, 子对象 Set）
     * @param <T>         主对象类型
     * @param <S>         子对象类型
     * @return 合并后的主对象列表；groupMap 为空时返回空列表
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

    /**
     * 将多个字段值拼接为组合 key，格式为 {@code val1#val2#...}。
     *
     * @param obj            源对象
     * @param keyExtractors  字段提取函数（可变参数）
     * @param <T>              元素类型
     * @return 组合 key 字符串
     */
    @SafeVarargs
    private static <T> String buildKey(T obj, Function<T, ?>... keyExtractors) {
        StringBuilder sb = new StringBuilder();
        for (Function<T, ?> extractor : keyExtractors) {
            Object val = extractor.apply(obj);
            sb.append(val).append("#");
        }
        return sb.toString();
    }

    /**
     * 根据 selectedMap 对列表进行“选中标记”或“兜底标记最大等级”的处理。
     *
     * <p>执行逻辑如下：</p>
     * <ol>
     *     <li>如果 list 为空，直接返回</li>
     *     <li>先判断 firstCondition（全局优先条件）：
     *         <ul>
     *             <li>如果成立，直接执行 maxLevelSetter 并返回</li>
     *         </ul>
     *     </li>
     *     <li>否则遍历 list：
     *         <ul>
     *             <li>通过 keyGenerator 生成 key</li>
     *             <li>如果 selectedMap 包含该 key，则执行 checkSetter 标记该元素</li>
     *         </ul>
     *     </li>
     *     <li>如果整个列表没有任何匹配项，则执行 maxLevelSetter 作为兜底逻辑</li>
     * </ol>
     *
     * <p>典型应用场景：</p>
     * <ul>
     *     <li>命中集合优先标记逻辑</li>
     *     <li>无命中时默认升级/标记最高等级</li>
     * </ul>
     *
     * @param list           待处理的数据列表
     * @param selectedMap    命中集合（key-value结构，仅使用 key）
     * @param firstCondition 全局优先条件（满足则直接进入 max 逻辑）
     * @param keyGenerator   从 T 对象中提取 key 的函数
     * @param checkSetter    命中时对元素执行的标记操作（如 setSelected=true）
     * @param maxLevelSetter 未命中任何元素时执行的兜底逻辑（如标记 max level）
     */
    public static <T> void markSelectedOrMax(List<T> list, Map<String, ?> selectedMap,
                                             Function<Map<String, ?>, Boolean> firstCondition,
                                             Function<T, String> keyGenerator,
                                             Consumer<T> checkSetter, Runnable maxLevelSetter
    ) {
        if (ObjectUtil.isEmpty(list)) {
            return;
        }

        if (firstCondition.apply(selectedMap)) {
            maxLevelSetter.run();
            return;
        }

        boolean matched = list.stream().anyMatch(item -> {
            String key = keyGenerator.apply(item);
            if (selectedMap.containsKey(key)) {
                checkSetter.accept(item);
                return true;
            }
            return false;
        });

    }

    /**
     * 从对象列表中提取某个字段
     *
     * @param list   源列表
     * @param mapper 字段提取函数
     * @param <T>    源元素类型
     * @param <R>    目标字段类型
     * @return 提取后的列表；list 为空时返回空列表
     */
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        if (ObjectUtil.isEmpty(list)) {
            return List.of();
        }
        return list.stream().map(mapper).toList();
    }

    /**
     * List 转 Map
     *
     * @param list      列表
     * @param keyMapper key 提取函数
     * @param <T>       元素类型
     * @param <K>       key 类型
     * @return {@code Map<K, T>}
     */
    public static <T, K> Map<K, T> toMap(List<T> list, Function<T, K> keyMapper) {
        if (ObjectUtil.isEmpty(list)) {
            return Map.of();
        }
        return list.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
    }

    /**
     * List 转 Map（自定义 key 与 value）
     *
     * @param list        源集合
     * @param keyMapper   key 提取函数
     * @param valueMapper value 提取函数
     * @param <T>           元素类型
     * @param <K>           key 类型
     * @param <V>           value 类型
     * @return {@code Map<K, V>}；list 为空时返回空 Map
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> list, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        if (ObjectUtil.isEmpty(list)) {
            return Map.of();
        }
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * 求两个列表的差集（a 中存在但 b 中不存在的元素）。
     *
     * <p>使用 {@link Set#contains} 判断元素是否存在于 b，依赖元素的 {@code equals} 方法。</p>
     *
     * @param a 被减列表
     * @param b 减数列表
     * @param <T> 元素类型
     * @return a - b；a 为空时返回空列表，b 为空时返回 a
     */
    public static <T> List<T> difference(List<T> a, List<T> b) {
        if (ObjectUtil.isEmpty(a)) {
            return List.of();
        }
        if (ObjectUtil.isEmpty(b)) {
            return a;
        }

        Set<T> setB = new HashSet<>(b);

        return a.stream().filter(e -> !setB.contains(e)).toList();
    }

    /**
     * List 转 Map，支持 key 冲突时的覆盖策略。
     *
     * @param list      列表
     * @param keyMapper key 提取函数
     * @param override  key 冲突时是否用后者覆盖前者
     * @param <T>       元素类型
     * @param <K>       key 类型
     * @return {@code Map<K, T>}；list 为空时返回空 Map
     */
    public static <T, K> Map<K, T> toMap(List<T> list, Function<T, K> keyMapper, boolean override) {
        if (ObjectUtil.isEmpty(list)) {
            return Map.of();
        }
        return list.stream().collect(Collectors.toMap(keyMapper, Function.identity(),
                (v1, v2) -> override ? v2 : v1));
    }

}