package com.kun.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记 TSV 文件中字段对应的列索引。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TsvIndex {

    /**
     * TSV 列索引（从 0 开始）。
     *
     * @return 列索引
     */
    int index();

}
