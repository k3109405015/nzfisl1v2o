package com.kun.enums;

import lombok.Getter;

/**
 * HTTP Content-Type 枚举，封装常用 MIME 类型。
 *
 * @author GaoYu
 */
@Getter
public enum ContentTypeEnum {

    /**
     * JSON 类型
     */
    JSON("application/json"),

    /**
     * 表单提交（key=value）
     */
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),

    /**
     * Multipart 表单（文件上传）
     */
    MULTIPART_FORM_DATA("multipart/form-data"),

    /**
     * 纯文本
     */
    TEXT_PLAIN("text/plain"),

    /**
     * XML
     */
    APPLICATION_XML("application/xml"),

    /**
     * HTML
     */
    TEXT_HTML("text/html");

    /**
     * Content-Type 字符串值
     */
    private final String value;

    /**
     * 构造方法
     */
    ContentTypeEnum(String value) {
        this.value = value;
    }
}