package com.kun.tools;

/**
 * 断言工具类
 *
 * @author kun
 */
public class AssertUtil {

    /**
     * 工具类，禁止实例化。
     */
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

    /**
     * 批量非空校验，任一对象为空则抛出异常。
     *
     * @param message 异常信息
     * @param objs    待校验对象
     */
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

    /**
     * 校验对象必须为空，非空则抛出异常。
     *
     * @param obj     校验对象
     * @param message 异常信息
     */
    public static void isNull(Object obj, String message) {
        if (!ObjectUtil.isEmpty(obj)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 校验表达式为 true，否则抛出异常。
     *
     * @param expression 布尔表达式
     * @param message    异常信息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

}