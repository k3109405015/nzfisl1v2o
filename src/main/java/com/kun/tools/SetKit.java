package com.kun.tools;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Set 集合操作工具类。
 */
public class SetKit {

    /**
     * 将 List 元素映射为 Set。
     *
     * @param list   源列表
     * @param mapper 映射函数
     * @param <T>    源元素类型
     * @param <R>    目标元素类型
     * @return 映射后的 Set
     */
    public static <T, R> Set<R> mapToSet(List<T> list, Function<T, R> mapper) {
        if (ObjectUtil.isEmpty(list)) {
            return Collections.emptySet();
        }
        return list.stream().map(mapper).collect(Collectors.toSet());
    }

}
