package com.kun.tools;

import com.kun.enums.ImageMimeType;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * 图片处理工具类，提供 Base64 编码能力。
 *
 * @author GaoYu
 */
public class PictureUtil {

    /**
     * 将 {@link ByteArrayOutputStream} 中的图片数据转换为 Data URI 格式的 Base64 字符串。
     *
     * @param byteArrayOutputStream 图片字节流，为空或长度为 0 时返回 null
     * @param type                  图片 MIME 类型
     * @return Data URI 格式的 Base64 字符串，如 {@code data:image/png;base64,...}
     */
    public static String toBase64(ByteArrayOutputStream byteArrayOutputStream, ImageMimeType type) {
        if (byteArrayOutputStream == null || byteArrayOutputStream.size() == 0) {
            return null;
        }
        // 转 byte[]
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return toBase64(imageBytes, type);
    }

    /**
     * 将图片字节数组转换为 Data URI 格式的 Base64 字符串。
     *
     * @param bytes 图片字节数组，为空时返回 null
     * @param type  图片 MIME 类型
     * @return Data URI 格式的 Base64 字符串，如 {@code data:image/png;base64,...}
     */
    public static String toBase64(byte[] bytes, ImageMimeType type) {
        if (ObjectUtil.isEmpty(bytes)) {
            return null;
        }
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:" + type.getMimeType() + ";base64," + base64;
    }

}
