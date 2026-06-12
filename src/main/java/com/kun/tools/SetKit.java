package com.kun.tools;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SetKit {

    public static <T, R> Set<R> mapToSet(List<T> list, Function<T, R> mapper) {
        if (ObjectUtil.isEmpty(list)) {
            return Collections.emptySet();
        }
        return list.stream().map(mapper).collect(Collectors.toSet());
    }

}
