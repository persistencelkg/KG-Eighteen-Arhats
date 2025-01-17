package org.lkg.utils.io;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.lkg.core.DynamicConfigManger;
import org.lkg.utils.FileUtil;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于google-zxing 生产二维码工具
 * Description: 支持本地文件、流、js-img
 * Author: 李开广
 * Date: 2024/11/19 3:37 PM
 */
@Slf4j
public class QRCodeUtil {

    // 用于设置QR二维码参数
    private static Map<EncodeHintType, Object> HINT_MAP = new HashMap<EncodeHintType, Object>() {
        {
            // 设置QR二维码的纠错级别（H为最高级别）具体级别信息 图案不一样，结果一样
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            put(EncodeHintType.CHARACTER_SET, "utf-8");// 设置编码方式
            put(EncodeHintType.MARGIN, 0);
        }
    };
    private static String BASE_64_URL = "data:image/png;base64,";


    /**
     * 基于base64 提供给端上渲染
     * @param content
     * @return
     */
    public static String generateQrcodeImageWithBase64(String content) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(1024);
        defaultSizeGenerateQrcode(content, arrayOutputStream);
        Base64.Encoder encoder = Base64.getEncoder();
        return BASE_64_URL + encoder.encodeToString(arrayOutputStream.toByteArray());
    }

    public static void defaultSizeGenerateQrcode(String content, OutputStream outputStream){
        generateQrcodeImageToStream(content, DynamicConfigManger.getInt("qr-img-width", 200), DynamicConfigManger.getInt("qr-img-height", 200), outputStream);
    }


    @Nullable
    private static void generateQrcodeImageToStream(String qrUrl, Integer width, Integer height, OutputStream outputStream) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrUrl, BarcodeFormat.QR_CODE, width, height, HINT_MAP);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        } catch (Exception e) {
            log.error("generateQrcodeImage exception:{}", e.getMessage(), e);
        }

    }


    public static void main(String[] args) throws FileNotFoundException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(FileUtil.getOrCreateFileInClass(QRCodeUtil.class, "a.png"))) {
            defaultSizeGenerateQrcode("lkg", fileOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(generateQrcodeImageWithBase64("wkx forever"));

    }

}
