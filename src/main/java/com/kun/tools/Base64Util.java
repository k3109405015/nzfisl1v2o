package com.kun.tools;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 编码工具类。
 */
public class Base64Util {

    /**
     * Base64 URL-safe 编码（去padding）
     *
     * @param raw 原始字符串
     * @return URL安全Base64字符串
     */
    public static String encodeUrl(String raw) {
        if (ObjectUtil.isEmpty(raw)) {
            return null;
        }
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

}
