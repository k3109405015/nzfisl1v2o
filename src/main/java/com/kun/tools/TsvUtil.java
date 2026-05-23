package com.kun.tools;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.kun.annotation.TsvIndex;
//import org.springframework.core.convert.ConversionService;
//import org.springframework.util.Assert;
//import org.springframework.util.ReflectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TsvUtil {

    private TsvUtil() {
    }

    /**
     * 读取 TSV 文件并映射为 Java Bean 列表。
     * TSV 示例：
     * name\tage
     * Tom\t18
     * Jack\t20
     * 注意：
     * - 文件必须包含表头
     * - 字段名必须与 Java 属性一致（区分大小写）
     *
     * @param file   TSV 文件
     * @param tClass 目标类型
     * @param <T>    泛型
     * @return 对象列表
     */
    public static <T> List<T> readTsv(File file, Class<T> tClass) {
        AssertUtil.notNull(file, "TSV file must exist");
        AssertUtil.notNull(tClass, "Target class must not be null");
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator('\t');
        try {
            try (MappingIterator<T> it = mapper.readerFor(tClass).with(schema).readValues(file)) {
                return it.readAll();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read TSV file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 读取无表头 TSV 文件并映射为 Java Bean 列表。
     * 示例：
     * 文件内容：
     * a1    b1
     * a2    b2
     * readTsvRaw(file, Demo.class)
     *
     * @param file   TSV 文件（无表头）
     * @param tClass 目标 Java 类型
     * @param <T>    泛型类型
     * @return 映射后的对象列表
     */
    public static <T> List<T> readTsvRaw(File file, Class<T> tClass) {
        List<String[]> rows = readTsvRaw(file);
        return mapRows(rows, tClass);
    }

    private static List<String[]> readTsvRaw(File file) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(line.split("\t", -1)); // -1 保留空列
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    private static <T> List<T> mapRows(List<String[]> list, Class<T> tClass) {
        ArrayList<T> result = new ArrayList<>();
        for (String[] row : list) {
            T obj;
            try {
                obj = tClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            List<Field> fields = BeanUtil.getAllFields(tClass);
            for (Field field : fields) {
                TsvIndex annotation = field.getAnnotation(TsvIndex.class);
                if (annotation != null) {
                    int index = annotation.index();
                    AssertUtil.isTrue(index <= row.length,
                            "Index out of bounds for TSV row");
                    Object convert = TypeConvertUtil.convert(row[index], field.getType());
                    try {
                        field.set(obj, convert);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to set field: " + field.getName(), e);
                    }
                }
            }
            result.add(obj);
        }
        return result;
    }

}
