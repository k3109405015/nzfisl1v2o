package com.kun.tools;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanUtil {

    private BeanUtil() {
    }

    /**
     * 从 Map 或 Java Bean 中获取属性值
     *
     * @param target 目标对象
     * @param key    属性名
     * @param clazz  返回类型
     * @param <T>    泛型类型
     * @return Optional 包装的属性值，安全避免空指针
     */
    public static <T> T getProperty(Object target, String key, Class<T> clazz) {
        AssertUtil.notNull("target, key and clazz must not be null", target, key, clazz);
        if (target instanceof Map<?, ?> map) {
            Object value = map.get(key);
            return clazz.cast(value);
        } else {
            Object value = getFieldValue(target, key);
            return clazz.cast(value);
        }
    }

    public static <T> T copy(Object source, Class<T> clazz) {
        AssertUtil.notNull("Source and clazz must not be null", source, clazz);
        T target;
        BeanInfo classBeanInfo;
        BeanInfo targetBeanInfo;
        try {
            target = clazz.getDeclaredConstructor().newInstance();
            // 获取bean的信息
            targetBeanInfo = Introspector.getBeanInfo(source.getClass());
            classBeanInfo = Introspector.getBeanInfo(clazz);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Failed to copy properties from " + source.getClass().getName() + " to "
                            + clazz.getName(), e);
        } catch (IntrospectionException e) {
            throw new IllegalStateException(
                    "Failed to introspect bean properties for class: " + clazz.getName(), e);
        }
        // 具体转换逻辑
        Map<String, PropertyDescriptor> classPdMap = Arrays.stream(targetBeanInfo.getPropertyDescriptors())
                .filter(v -> !v.getName().equals("class"))
                .collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity()));

        PropertyDescriptor[] descriptors = classBeanInfo.getPropertyDescriptors();

        for (PropertyDescriptor sourcePd : descriptors) {
            String name = sourcePd.getName();
            // 跳过 class
            if ("class".equals(name)) {
                continue;
            }
            // 获取改变量写入的方法
            Method writeMethod = sourcePd.getWriteMethod();
            if (writeMethod == null) {
                continue;
            }

            PropertyDescriptor targetPd = classPdMap.get(name);
            if (targetPd == null) {
                continue;
            }
            // 获取该方法读入的方法
            Method readMethod = targetPd.getReadMethod();
            if (readMethod == null) {
                continue;
            }

            try {
                // 执行 getter → setter
                Object value = readMethod.invoke(source);
                // 直接赋值（浅拷贝核心）
                writeMethod.invoke(target, value);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new IllegalStateException(
                        "Failed to copy property '" + sourcePd.getName()
                                + "' from " + source.getClass().getName()
                                + " to " + target.getClass().getName(), e
                );
            }
        }

        return target;
    }

    /**
     * 通过反射获取字段值（getter 优先，字段兜底）
     *
     * <p>规则：
     * 1. 优先调用 getter 方法获取值
     * 2. getter 返回 null 时，再读取字段值
     *
     * <p>说明：
     * - 会绕过 private 访问限制
     * - 反射调用性能低于普通方法调用
     *
     * @param target 目标对象（不能为空）
     * @param fieldName 字段名
     * @return 字段值或 getter 返回值
     */
    public static Object getFieldValue(Object target, String fieldName) {
        AssertUtil.notNull(target, "Target object cannot be null");
        Class<?> clazz = target.getClass();

        // 优先尝试 getter
        Object value = getValueByGetter(target, fieldName);
        if (value != null) {
            return value;
        }

        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(
                    "Field '" + fieldName + "' not found in class " + target.getClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access field '" + fieldName + "'", e);
        }
    }

    /**
     * 通过 getter 方法获取字段值。
     * <p>获取成员变量的get方法：
     * <pre>
     *     name -> getName()
     *     age  -> getAge()
     * </pre>
     *
     * @param target    目标对象
     * @param fieldName 字段名称
     * @return getter 返回值；若 getter 不存在或调用失败则返回 null
     */
    private static Object getValueByGetter(Object target, String fieldName) {
        try {
            Class<?> clazz = target.getClass();
            String getter = "get" + StringUtil.capitalize(fieldName);
            Method method = clazz.getMethod(getter);
            return method.invoke(target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * 获取类及其父类的所有字段，子类字段优先（同名字段覆盖父类）
     *
     * <p>规则：</p>
     * <ul>
     *     <li>子类字段优先级高于父类</li>
     *     <li>同名字段以子类为准</li>
     *     <li>包含 private / protected / public 字段</li>
     *     <li>不包含 Object.class</li>
     * </ul>
     *
     * @param clazz 目标类
     * @return 去重后的字段列表（子类优先）
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        AssertUtil.notNull(clazz, "Class must not be null");
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                field.setAccessible(true);
                fieldMap.put(field.getName(), field);
            }
            current = current.getSuperclass();
        }
        return fieldMap.values().stream().toList();
    }

}
