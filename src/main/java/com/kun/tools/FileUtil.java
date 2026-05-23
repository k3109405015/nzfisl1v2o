package com.kun.tools;

import com.kun.enums.FileType;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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

}
