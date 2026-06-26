package com.kun.tools;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.kun.annotation.TsvIndex;
import com.kun.enums.CharsetEnum;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * TSV（Tab 分隔值）文件读写工具类。
 *
 * @author GaoYu
 */
public class TsvUtil {

    private TsvUtil() {
    }

    private static final CsvMapper MAPPER = new CsvMapper();

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
        return readTsv(file, tClass, null);
    }

    /**
     * 读取 TSV 文件并映射为 Java Bean 列表，支持指定字符集。
     *
     * <p>文件必须包含表头，字段名须与 Java 属性名一致（区分大小写）。</p>
     *
     * @param file    TSV 文件
     * @param tClass  目标类型
     * @param charset 字符集，为 null 时使用默认编码
     * @param <T>     泛型
     * @return 对象列表
     */
    public static <T> List<T> readTsv(File file, Class<T> tClass, CharsetEnum charset) {
        AssertUtil.notNull(file, "TSV file must exist");
        AssertUtil.notNull(tClass, "Target class must not be null");
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader().withColumnSeparator('\t');
        CsvMapper mapper = new CsvMapper();
        try {
            MappingIterator<T> iterator = null;
            Reader reader = null;
            try {
                if (ObjectUtil.isEmpty(charset)) {
                    iterator = mapper.readerFor(tClass).with(csvSchema).readValues(file);
                } else {
                    reader = Files.newBufferedReader(file.toPath(), charset.getCharset());
                    iterator = mapper.readerFor(tClass).with(csvSchema).readValues(reader);
                }

                return iterator.readAll();
            } finally {
                if (!ObjectUtil.isEmpty(iterator)) {
                    iterator.close();
                }
                if (!ObjectUtil.isEmpty(reader)) {
                    reader.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
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

    /**
     * 读取无表头 TSV 文件并映射为 Java Bean 列表，支持指定字符集。
     *
     * <p>字段映射依赖 {@link TsvIndex} 注解指定的列索引。</p>
     *
     * @param file    TSV 文件（无表头）
     * @param tClass  目标 Java 类型
     * @param charset 字符集
     * @param <T>     泛型类型
     * @return 映射后的对象列表
     */
    public static <T> List<T> readTsvRaw(File file, Class<T> tClass, CharsetEnum charset) {
        List<String[]> rows = readTsvRaw(file, charset);
        return mapRows(rows, tClass);
    }

    /**
     * 将对象列表写入 TSV 文件。
     *
     * @param data    待写入的数据列表
     * @param clazz   数据类型
     * @param outFile 输出文件路径
     * @param <T>     泛型类型
     */
    public static <T> void writeTsv(List<T> data, Class<T> clazz, Path outFile) {
        AssertUtil.notNull(data, "TSV data must exist");
        AssertUtil.notNull(clazz, "Target class must not be null");
        AssertUtil.notNull(outFile, "Output file must not be null");
        CsvSchema schema = MAPPER.schemaFor(clazz).withHeader().withColumnSeparator('\t');
        try {
            MAPPER.writer(schema).writeValue(outFile.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write TSV file: " + outFile, e);
        }
    }

    /**
     * 读取无表头 TSV 文件的原始行数据，默认使用 UTF-8 编码。
     *
     * @param file TSV 文件
     * @return 按行拆分后的列数组列表
     */
    private static List<String[]> readTsvRaw(File file) {
        return readTsvRaw(file, CharsetEnum.UTF_8);
    }

    /**
     * 读取无表头 TSV 文件的原始行数据。
     *
     * @param file    TSV 文件
     * @param charset 字符集
     * @return 按行拆分后的列数组列表
     */
    private static List<String[]> readTsvRaw(File file, CharsetEnum charset) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset.getCharset()))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(line.split("\t", -1)); // -1 保留空列
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    /**
     * 将原始行数据映射为 Java Bean 列表。
     *
     * <p>通过 {@link TsvIndex} 注解将列索引映射到对象字段。</p>
     *
     * @param list   原始行数据
     * @param tClass 目标类型
     * @param <T>    泛型类型
     * @return 映射后的对象列表
     */
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
