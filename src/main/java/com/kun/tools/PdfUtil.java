package com.kun.tools;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * PDF 文本提取工具类，基于 Apache PDFBox 实现。
 */
public class PdfUtil {

    /**
     * 提取 PDF 文件中的全部文本内容
     *
     * <p>实现方式：
     * - 使用 PDFBox 加载 PDF 文档
     * - 按页遍历提取文本
     * - 逐页追加到 StringBuilder 中
     *
     * <p>特点：
     * - 按页处理，降低一次性内存压力
     * - 保证文本按页面顺序输出
     *
     * <p>注意：
     * - 适用于中小型 PDF 文件
     * - 大文件仍可能占用较高内存（取决于 PDFBox 解析机制）
     *
     * @param inputStream PDF 输入流（方法内会自动关闭）
     * @return 提取后的全文字符串
     * @throws UncheckedIOException PDF 解析失败时抛出
     */
    public static String pdfToString(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();

        try (inputStream;
             PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(inputStream))) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            int pages = document.getNumberOfPages();

            for (int i = 1; i <= pages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                sb.append(stripper.getText(document));
            }

            return sb.toString();

        } catch (IOException e) {
            throw new UncheckedIOException("PDF text extraction failed", e);
        }
    }

}
