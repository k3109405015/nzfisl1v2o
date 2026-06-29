package com.kun.tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IDUtil {

    /**
     * 生成带前缀的短 ID。
     *
     * <p>格式：{@code prefix_yyyyMMddHHmmssSSS} 经 Base64 URL-safe 编码后截取指定长度。</p>
     *
     * @param prefix  ID 前缀
     * @param length  截取长度；若编码结果更短则返回完整编码
     * @return 短 ID 字符串
     */
    public static String genShortId(String prefix, int length) {
        // 1. 时间格式：yyyyMMddHHmmssSSS（毫秒级）
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String timeStr = LocalDateTime.now().format(formatter);
        // 2. 拼接原始字符串
        String raw = prefix + "_" + timeStr;
        // 3. Base64 URL-safe 编码
        String base64 = Base64Util.encodeUrl(raw);
        // 4. 截取长度（防止越界）
        if (ObjectUtil.isEmpty(base64) || length > base64.length()) {
            return base64;
        }
        return base64.substring(0, length);
    }

}
