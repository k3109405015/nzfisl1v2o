package com.kun.tools;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class ObjectUtil {

    private ObjectUtil() {

    }

    public static boolean isEmpty(Object obj) {

        if (obj == null) {
            return true;
        }

        // String
        if (obj instanceof CharSequence str) {
            String value = str.toString();
            return value.isBlank() || "null".equalsIgnoreCase(value);
        }

        // Collection
        if (obj instanceof Collection<?> collection) {
            return collection.isEmpty();
        }

        // Map
        if (obj instanceof Map<?, ?> map) {
            return map.isEmpty();
        }

        // Array
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }

        return false;
    }

}
