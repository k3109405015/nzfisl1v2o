package com.kun.tools;


public class AssertUtil {

    private AssertUtil() {
    }

    /**
     * 通用非空校验
     *
     * @param obj     校验对象
     * @param message 异常信息
     */
    public static void notNull(Object obj, String message) {
        if (ObjectUtil.isEmpty(obj)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(String message, Object... objs) {
        if (objs == null || objs.length == 0) {
            throw new IllegalArgumentException("No objects to validate");
        }

        for (Object obj : objs) {
            if (ObjectUtil.isEmpty(obj)) {
                throw new IllegalArgumentException(message);
            }
        }

    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

}