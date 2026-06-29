package com.kun.tools;

import com.alibaba.fastjson2.JSON;

/**
 * JSON 序列化与反序列化工具类，基于 Fastjson2 实现。
 */
public final class JsonUtil {

    /** 工具类，禁止实例化。 */
    private JsonUtil() {
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param obj 源对象
     * @return JSON 字符串；obj 为 null 时返回 null
     */
    public static String toJson(Object obj) {
        return obj == null ? null : JSON.toJSONString(obj);
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的对象。
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 反序列化后的对象；json 为空时返回 null
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        return ObjectUtil.isEmpty(json) ? null : JSON.parseObject(json, clazz);
    }

}