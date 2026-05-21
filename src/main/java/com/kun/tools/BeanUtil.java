package com.kun.tools;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;

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
        if (target == null || !StringUtils.hasLength(key) || clazz == null) {
            return null;
        }
        if (target instanceof Map<?, ?> map) {
            Object value = map.get(key);
            return clazz.cast(value);
        } else {
            BeanWrapper wrapper = new BeanWrapperImpl(target);
            Assert.isTrue(wrapper.isReadableProperty(key),
                    "Property " + key + " has no readable getter in class " + clazz.getName());
            Object propertyValue = wrapper.getPropertyValue(key);
            return clazz.cast(propertyValue);
        }
    }

}
