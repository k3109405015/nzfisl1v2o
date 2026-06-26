package com.kun.enums;

import lombok.Getter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 字符集枚举，封装常用 {@link Charset} 实例。
 *
 * @author GaoYu
 */
@Getter
public enum CharsetEnum {

    /**
     * GBK 字符集。
     */
    GBK(Charset.forName("GBK")),

    /**
     * UTF-8 字符集。
     */
    UTF_8(StandardCharsets.UTF_8);

    /**
     * 对应的 {@link Charset} 实例。
     */
    private final Charset charset;

    /**
     * 构造字符集枚举。
     *
     * @param charset 字符集实例
     */
    CharsetEnum(Charset charset) {
        this.charset = charset;
    }

}
