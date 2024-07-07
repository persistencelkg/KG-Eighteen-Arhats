package org.lkg.simple;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * 提供流处理工具
 */
@Slf4j
public class IOStreamUtil {


    public static long copy(InputStream in, OutputStream out) throws IOException {
        Objects.requireNonNull(in, "No InputStream specified");
        Objects.requireNonNull(out, "No OutputStream specified");
        byte[] buffer = new byte[8192];
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
            close(in, out);
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
