package com.kun.tools;

import com.alibaba.fastjson2.JSON;

public final class JsonUtil {

    private JsonUtil() {
    }

    public static String toJson(Object obj) {
        return obj == null ? null : JSON.toJSONString(obj);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return ObjectUtil.isEmpty(json) ? null : JSON.parseObject(json, clazz);
    }

}