package com.kun.tools;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.kun.enums.ImageMimeType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片处理工具类，提供 Base64 编码能力。
 *
 * @author GaoYu
 */
public class PictureUtil {

    /**
     * 将 {@link ByteArrayOutputStream} 中的图片数据转换为 Data URI 格式的 Base64 字符串。
     *
     * @param byteArrayOutputStream 图片字节流，为空或长度为 0 时返回 null
     * @param type                  图片 MIME 类型
     * @return Data URI 格式的 Base64 字符串，如 {@code data:image/png;base64,...}
     */
    public static String toBase64(ByteArrayOutputStream byteArrayOutputStream, ImageMimeType type) {
        if (byteArrayOutputStream == null || byteArrayOutputStream.size() == 0) {
            return null;
        }
        // 转 byte[]
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return toBase64(imageBytes, type);
    }

    /**
     * 将图片字节数组转换为 Data URI 格式的 Base64 字符串。
     *
     * @param bytes 图片字节数组，为空时返回 null
     * @param type  图片 MIME 类型
     * @return Data URI 格式的 Base64 字符串，如 {@code data:image/png;base64,...}
     */
    public static String toBase64(byte[] bytes, ImageMimeType type) {
        if (ObjectUtil.isEmpty(bytes)) {
            return null;
        }
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:" + type.getMimeType() + ";base64," + base64;
    }

    /**
     * 二维码生成
     *
     * @param content 二维码内容
     * @param size    二维码大小
     * @return 二维码图片
     */
    public static BufferedImage qrCodeGenerator(String content, int size) {
        // 创建参数容器
        Map<EncodeHintType, Object> hints = new HashMap<>();
        // 设置容错等级（允许 logo 覆盖），就是高，扫一半也可以扫出来
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置白边（0 = 无白边）
        hints.put(EncodeHintType.MARGIN, 0);
        // ZXing 的编码器工厂
        // 参数一：内容，参数二：生成的类型是二维码
        // 参数三和四：宽高，参数五：规则配置。
        BitMatrix matrix;
        try {
            matrix = new MultiFormatWriter().encode(
                    content, BarcodeFormat.QR_CODE, size, size, hints
            );
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        int width = matrix.getWidth();
        int height = matrix.getHeight();

        // 创建图片画布，参数三是：带颜色的画布RGB
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 绘制二维码
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }
        return image;
    }


    /**
     * 添加 logo 到二维码中
     *
     * @param qr       二维码图片
     * @param logoPath logo 图片路径
     * @return 添加 logo 后的二维码图片
     */
    public static BufferedImage addLogo(BufferedImage qr, String logoPath) {
        // 读取 logo 图片
        BufferedImage logo;
        try {
            logo = ImageIO.read(new File(logoPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 根据二维码大小，动态计算 logo 要占多少空间
        int qrSize = qr.getWidth();
        // 除以 5，logo 占二维码的 1/5
        int logoSize = qrSize / 5;
        // 1px像素边框
        int border = 1;
        int boxSize = logoSize + border * 2;

        // 把原始 logo 压缩/缩放成 logoSize × logoSize 的图片
        // 图片缩小 / 放大
        // SCALE_SMOOTH = 平滑缩放、清晰、速度稍慢
        Image scaledLogo = logo.getScaledInstance(
                logoSize, logoSize,
                Image.SCALE_SMOOTH);
        // 开启高质量模式
        // 绘画器
        Graphics2D g = qr.createGraphics();
        // KEY_ANTIALIASING = 抗锯齿
        // VALUE_ANTIALIAS_ON = 开启
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // KEY_RENDERING = 渲染策略
        // VALUE_RENDER_QUALITY = 高质量模式
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 把 logo 放到二维码正中间
        int x = (qrSize - logoSize) / 2;
        int y = (qrSize - logoSize) / 2;

        // 1. 先画像素边框（矩形框）
        g.setColor(Color.BLACK); // 你也可以换成品牌色
        g.fillRect(x, y, boxSize, boxSize);

        // 2. 再在中间画 logo（留1px边框）
        g.drawImage(scaledLogo, x + border, y + border, null);
        g.dispose();
        return qr;
    }

    /**
     * 生成带 logo 的二维码
     *
     * @param content  二维码内容
     * @param qrSize   二维码大小
     * @param logoPath logo 图片路径
     * @return 带 logo 的二维码图片
     */
    public static BufferedImage generateQrWithLogo(String content, Integer qrSize, String logoPath) {
        AssertUtil.notNull("content and qrSize must not be null", content, qrSize);
        AssertUtil.notNull(logoPath, "logoPath must not be null");
        BufferedImage bufferedImage = qrCodeGenerator(content, qrSize);
        return addLogo(bufferedImage, logoPath);
    }

    /**
     * 生成带 logo 的二维码并保存到指定路径
     *
     * @param content  二维码内容
     * @param qrSize   二维码大小
     * @param logoPath logo 图片路径
     * @param toPath   二维码保存路径
     */
    public static void generateQrCodeWithLogoToFile(String content, Integer qrSize,
                                                    String logoPath, String toPath) {
        AssertUtil.notNull(toPath, "toPath must not be null");
        BufferedImage bufferedImage = generateQrWithLogo(content, qrSize, logoPath);
        writeImage(toPath, bufferedImage);
    }

    /**
     * 二维码生成
     *
     * @param content 二维码内容
     * @param qrSize  二维码大小
     * @param toPath  二维码保存路径
     */
    public static void generateQrCodeWithLogoToFile(String content, Integer qrSize, String toPath) {
        AssertUtil.notNull("content and qrSize must not be null", content, qrSize);
        AssertUtil.notNull(toPath, "toPath must not be null");
        BufferedImage bufferedImage = qrCodeGenerator(content, qrSize);
        writeImage(toPath, bufferedImage);
    }

    private static void writeImage(String toPath, BufferedImage bufferedImage) {
        try {
            ImageIO.write(bufferedImage, "png", new File(toPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
