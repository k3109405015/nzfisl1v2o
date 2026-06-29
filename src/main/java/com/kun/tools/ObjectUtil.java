package com.kun.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 对象空值判断工具类。
 */
public class ObjectUtil {

    /** 工具类，禁止实例化。 */
    private ObjectUtil() {
    }

    /**
     * 判断对象是否为空。
     *
     * <p>支持 null、空字符串、空集合、空 Map、空数组。</p>
     *
     * @param obj 待判断对象
     * @return true 表示为空
     */
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

    /**
     * 判断对象是否非空。
     *
     * @param obj 待判断对象
     * @return true 表示非空
     */
    public static boolean notEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断对象是否“空 Bean”（所有字段均为 null）
     *
     * <p>判断规则：
     * - 遍历对象所有字段（包含父类字段）
     * - 任一字段值不为 null，则返回 false
     * - 所有字段均为 null，则返回 true
     *
     * <p>说明：
     * - 通过反射获取字段值
     * - 不判断空字符串、空集合等“语义空值”
     *
     * @param obj 目标对象
     * @return true：所有字段均为 null；false：存在非 null 字段
     */
    public static boolean isEmptyBean(Object obj) {
        if (obj == null) {
            return true;
        }
        List<Field> fields = BeanUtil.getAllFields(obj.getClass());
        for (Field field : fields) {
            Object value = BeanUtil.getFieldValue(obj, field.getName());
            if (value != null) {
                return false;
            }
        }
        return true;
    }
}
