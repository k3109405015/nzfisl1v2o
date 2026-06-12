package com.kun.tools;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.kun.enums.CharsetEnum;

import java.util.List;

public class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * 读取 Excel 文件并映射为指定类型的对象列表（基于 EasyExcel）
     *
     * <p>该方法使用 EasyExcel 进行文件解析，并将 Excel 数据按表头映射为指定 Java Bean。</p>
     *
     * @param <T>      泛型类型，对应 Excel 每一行映射的对象类型
     * @param filePath Excel 文件路径，不能为空
     * @param clazz    目标映射类型（Java Bean class），不能为空
     * @param sheetNo  指定读取的 sheet 页索引，从 0 开始，必须 ≥ 0
     * @param charset
     * @return 解析后的对象列表，如果 sheet 为空则返回空集合
     */
    public static <T> List<T> readExcel(String filePath, Class<T> clazz, Integer sheetNo, CharsetEnum charset) {
        AssertUtil.notNull("filePath and clazz must not be null", filePath, clazz);
        AssertUtil.isTrue(sheetNo > -1, "sheetNo must be greater than or equal to 0");
        ExcelReaderBuilder builder = EasyExcel.read(filePath).head(clazz);
        if (!ObjectUtil.isEmpty(charset)) {
            builder.charset(charset.getCharset());
        }
        return builder.sheet(sheetNo).doReadSync();
    }

    public static <T> List<T> readExcel(String filePath, Class<T> clazz) {
        return readExcel(filePath, clazz, 0, null);
    }

    public static <T> List<T> readExcel(String filePath, Class<T> clazz, CharsetEnum charset) {
        return readExcel(filePath, clazz, 0, charset);
    }

}
