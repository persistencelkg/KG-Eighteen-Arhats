package org.lkg.utils.office;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.lkg.enums.StringEnum;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/9 10:33 AM
 */
public class ExcelUtils {

    private static final String DEFAULT_FOLDER = "office";


    /**
     * 默认存在target编译目录下的office目录下
     *
     * @param fileName
     * @return
     */
    public static Workbook createEmptyWorkBook() {
        HSSFWorkbook sheets = new HSSFWorkbook();
        sheets.createSheet();
        return sheets;
    }

    public static OutputStream getDefaultOutputStream(String fileName) throws IOException {
        URL resource = ExcelUtils.class.getClassLoader().getResource("");
        if (Objects.isNull(resource)) {
            throw new RuntimeException("loss resources dir");
        }
        File file = new File(resource.getPath() + File.separator + DEFAULT_FOLDER);
        if (!file.exists() && !file.mkdirs()) {
            throw new IOException(file.getPath() + "文件权限不足，无法创建");
        }
        return Files.newOutputStream(Paths.get(file.getPath() + File.separator + fileName));
    }


    public static Workbook createWorkBookFromUrl(String url) {
        // TODO 1.异常处理  2. 内存限制
        try (InputStream inputStream = new URL(url.replace(StringEnum.SPACE, "%20")).openStream()) {
            return createWorkBookFromInputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Workbook createWorkBookFromInputStream(InputStream inputStream) {
        // TODO 异常处理
        return createWorkBookFromInputStream(inputStream, null);
    }

    public static Workbook createWorkBookWithLocal(String fileName) throws FileNotFoundException {
        InputStream resourceAsStream = ExcelUtils.class.getClassLoader().getResourceAsStream(DEFAULT_FOLDER + File.separator + fileName);
        if (Objects.isNull(resourceAsStream)) {
            throw new FileNotFoundException(fileName + " not found,please check");
        }
        return createWorkBookFromInputStream(resourceAsStream, null);
    }


    public static Workbook createWorkBookFromInputStream(InputStream inputStream, String password) {
        try {
            Workbook sheets = WorkbookFactory.create(inputStream, password);
            if (sheets.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException("文档为空");
            }
            return sheets;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
