package com.kun.tools;

public class StringUtil {

    private StringUtil() {
    }

    public static String capitalize(String name) {
        AssertUtil.notNull(name, "name must not be null or blank");
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}
