package com.kun.tools;

import java.time.LocalDateTime;

/**
 * ID 生成工具类。
 */
public class IDUtil {

    public static String genQrcodeId(String libCode) {
        // Java：用纳秒补足微秒效果（取前6位）
        LocalDateTime now = LocalDateTime.now();
        String timeStr = LocalDateTimeUtil.now() + String.format("%06d", now.getNano() / 1000);
        String raw = libCode + "_" + timeStr;
        String b64 = Base64Util.encodeUrl(raw);
        return b64.substring(0, 15);
    }

}
