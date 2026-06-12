package com.kun.enums;

import lombok.Getter;

import java.nio.charset.Charset;

@Getter
public enum CharsetEnum {

    GBK(Charset.forName("GBK"));

    private final Charset charset;

    CharsetEnum(Charset charset) {
        this.charset = charset;
    }

}
