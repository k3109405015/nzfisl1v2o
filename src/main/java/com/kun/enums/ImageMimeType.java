package com.kun.enums;

import lombok.Getter;

/**
 * 图片 MIME 类型枚举。
 *
 * @author GaoYu
 */
@Getter
public enum ImageMimeType {

    /**
     * PNG 图片。
     */
    PNG("image/png"),

    /**
     * JPG 图片。
     */
    JPG("image/jpeg"),

    /**
     * JPEG 图片。
     */
    JPEG("image/jpeg"),

    /**
     * GIF 图片。
     */
    GIF("image/gif"),

    /**
     * BMP 图片。
     */
    BMP("image/bmp"),

    /**
     * WebP 图片。
     */
    WEBP("image/webp");

    /**
     * MIME 类型字符串。
     */
    private final String mimeType;

    /**
     * 构造图片 MIME 类型枚举。
     *
     * @param mimeType MIME 类型字符串
     */
    ImageMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
