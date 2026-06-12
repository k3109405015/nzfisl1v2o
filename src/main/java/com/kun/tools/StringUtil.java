package com.kun.tools;

public class StringUtil {

    private StringUtil() {
    }

    public static String capitalize(String name) {
        AssertUtil.notNull(name, "name must not be null or blank");
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * 判断第一个字符是否为字母
     */
    public static boolean isFirstCharLetter(String str) {
        return !ObjectUtil.isEmpty(str) && Character.isLetter(str.charAt(0));
    }


}
