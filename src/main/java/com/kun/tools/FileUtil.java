package com.kun.tools;

import com.kun.enums.FileType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

    private FileUtil() {
    }

    /**
     * 获取一个File
     *
     * @param input 文件路径或者目录
     */
    public static File getFile(String input) {
        AssertUtil.notNull(input, "File path must not be null or blank");
        return new File(input);
    }

    /**
     * 获取指定目录下的文件或目录
     *
     * @param directoryPath 目录路径
     * @param fileType      类型（文件、目录、所有）
     * @return 返回指定类型的文件或目录列表
     */
    public static List<File> listFilesOrDirs(String directoryPath, FileType fileType) {
        return listFilesOrDirs(getFile(directoryPath), fileType);
    }

    /**
     * 获取指定目录下的文件或目录
     *
     * @param file     目录
     * @param fileType 类型（文件、目录、所有）
     * @return 返回指定类型的文件或目录列表
     */
    public static List<File> listFilesOrDirs(File file, FileType fileType) {
        List<File> files = listFilesAndDirs(file, fileType);
        files.removeIf(v -> v.equals(file));
        return files;
    }

    private static List<File> listFilesAndDirs(File file, FileType fileType) {
        AssertUtil.isTrue(file.exists(), "Directory does not exist: " + file.getPath());
        AssertUtil.isTrue(file.isDirectory(), "Path is not a directory: " + file.getPath());
        File[] files = file.listFiles();
        if (ObjectUtil.isEmpty(files)) {
            return List.of();
        }
        Stream<File> stream = Arrays.stream(files);
        if (fileType == FileType.FILE) {
            stream = stream.filter(File::isFile);
        }
        if (fileType == FileType.DIRECTORY) {
            stream = stream.filter(File::isDirectory);
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 确保目录存在（不存在则自动创建）
     * 如果目录已存在，则不会做任何操作（幂等）。
     *
     * @param dirPath 目录路径字符串，不能为空或空白
     * @return 创建完成后的 Path 对象
     * @throws IllegalArgumentException 如果 dirPath 为空或空白
     * @throws RuntimeException         如果创建目录失败（例如权限不足）
     */
    public static Path ensureDirExists(String dirPath) {
        AssertUtil.notNull(dirPath, "dirPath cannot be null or blank");
        Path path = Paths.get(dirPath);
        try {
            return Files.createDirectories(path);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to create directory: " + dirPath, e);
        }
    }

    /**
     * 生成带时间戳的文件
     *
     * <p>文件名格式：
     * prefix_yyyyMMddHHmmsssuffix
     *
     * @param dirPath 目录路径（不能为空）
     * @param prefix  文件名前缀
     * @param suffix  文件后缀（建议包含点，如 ".txt"）
     * @return 创建目标文件 Path
     */
    public static Path createFile(String dirPath, String prefix, String suffix) {
        AssertUtil.notNull(dirPath, "dirPath cannot be null or blank");
        AssertUtil.notNull(prefix, "prefix cannot be null or blank");
        AssertUtil.notNull(suffix, "suffix cannot be null or blank");
        Path dir = ensureDirExists(dirPath);
        String fileName = prefix + "_" + LocalDateTimeUtil.now() + suffix;
        return dir.resolve(fileName);
    }

    /**
     * 判断文件是否匹配指定扩展名（忽略大小写）
     *
     * @param filePath 文件路径或文件名
     * @param exts     扩展名（可多个，如 "xls", ".tsv"）
     * @return 是否匹配
     */
    public static boolean hasExtension(String filePath, String... exts) {
        AssertUtil.notNull(filePath, "filePath cannot be null or blank");
        AssertUtil.notNull(exts, "exts cannot be null or empty");
        String name = filePath.toLowerCase();
        return Arrays.stream(exts).filter(Objects::nonNull).map(String::toLowerCase)
                .map(ext -> ext.startsWith(".") ? ext : "." + ext).anyMatch(name::endsWith);
    }

}
