package org.lkg.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Objects;

/**
 * 提供流处理工具
 */
@Slf4j
public class IOStreamUtil {

    public static final int DEFAULT_BUFFER = 4 * 1024;

    public static final int BUFFER_8_K = DEFAULT_BUFFER << 1;

    public static final int BUFFER_16_K = BUFFER_8_K << 1;

    public static final int BUFFER_64_K = BUFFER_16_K << 1;


    public static long copy(File file, OutputStream out, boolean circle) {
        if (Objects.isNull(file) || !file.exists()) {
            return 0L;
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return copy(inputStream, out, circle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(inputStream);
        }
    }

    public static long copy(InputStream in, OutputStream out) throws IOException {
        return copy(in, out, false);
    }

    public static long copy(InputStream in, OutputStream out, boolean circle) throws IOException {
        Objects.requireNonNull(in, "No InputStream specified");
        Objects.requireNonNull(out, "No OutputStream specified");
        // according buffer
        if (in instanceof BufferedInputStream) {
            BufferedInputStream buffIn = (BufferedInputStream) in;
            int bytesRead;
            while ((bytesRead = buffIn.read()) != -1) {
                out.write(bytesRead);
            }
            return -1L;
        }
        byte[] buffer = new byte[BUFFER_8_K];
        try {
            long byteCount;
            int bytesRead;
            for (byteCount = 0L; (bytesRead = in.read(buffer)) != -1; byteCount += (long) bytesRead) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            return byteCount;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } finally {
            if (!circle) {
                close(in, out);
            } else {
                close(in);
            }
        }
    }

    public static void close(Closeable... closeables) {
        if (ObjectUtil.isEmpty(closeables)) {
            return;
        }
        try {
            for (Closeable closeable : closeables) {
                closeable.close();
            }
        } catch (Exception e) {
            log.error("手动关闭数据流异常:{}", e.getMessage(), e);
        }

    }
}
