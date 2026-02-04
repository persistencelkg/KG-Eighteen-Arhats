package org.lkg.utils.office;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.lkg.enums.StringEnum;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        File file = getDefaultResource();
        return Files.newOutputStream(Paths.get(file.getPath() + File.separator + fileName));
    }

    @NotNull
    private static File getDefaultResource() throws IOException {
        URL resource = ExcelUtils.class.getClassLoader().getResource("");
        if (Objects.isNull(resource)) {
            throw new RuntimeException("loss resources dir");
        }
        File file = new File(resource.getPath() + File.separator + DEFAULT_FOLDER);
        if (!file.exists() && !file.mkdirs()) {
            throw new IOException(file.getPath() + "文件权限不足，无法创建");
        }
        return file;
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

    public static Workbook createWorkBookWithLocal(String fileName) throws IOException {

        InputStream resourceAsStream = null;
        resourceAsStream = ExcelUtils.class.getClassLoader().getResourceAsStream(DEFAULT_FOLDER + File.separator + fileName);

        return createWorkBookFromInputStream(resourceAsStream, null);
    }


    public static Workbook createWorkBookFromInputStream(InputStream inputStream, String password) {
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            if (!isValidExcel(bytes)) {
                throw new IllegalArgumentException("文档不合法");
            }
            Workbook sheets = WorkbookFactory.create(new ByteArrayInputStream(bytes), password);
            if (sheets.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException("文档为空");
            }
            return sheets;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidExcel(byte[] bytes) {
        if (bytes.length < 8) return false;

        // XLSX格式检查
        boolean isXlsx = bytes[0] == 0x50 && bytes[1] == 0x4B;

        // XLS格式检查
        boolean isXls = bytes[0] == (byte)0xD0 && bytes[1] == (byte)0xCF &&
                bytes[2] == (byte)0x11 && bytes[3] == (byte)0xE0;

        return isXlsx || isXls;
    }

    public static class PartitionedExcelWriter {
        public void processLargeExcel(Workbook workbook, int threadCount, String filePath) throws Exception {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();
            int partitionSize = totalRows / threadCount;

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<Void>> futures = new ArrayList<>();

            // 将Excel按行分区，每个线程处理一个区域
            for (int i = 0; i < threadCount; i++) {
                int startRow = i * partitionSize;
                int endRow = (i == threadCount - 1) ? totalRows : (i + 1) * partitionSize;

                futures.add(executor.submit(() -> {
                    // 注意：这里每个线程操作的区域不重叠
                    processRows(sheet, startRow, endRow);
                    return null;
                }));
            }

            // 等待所有线程完成
            for (Future<Void> future : futures) {
                future.get();
            }

            executor.shutdown();

            // 将工作簿写入文件
            try (OutputStream fos = ExcelUtils.getDefaultOutputStream(filePath)) {
                workbook.write(fos);
                fos.close();
                workbook.close();
            }
        }

        private void processRows(Sheet sheet, int startRow, int endRow) {
            for (int i = startRow; i < endRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    row = sheet.createRow(i);
                }
                for (int j = 5; j < 26; j++) {
                    // 写入数据到指定单元格
                    Cell cell = row.createCell(j);
                    cell.setCellValue(UUID.randomUUID().toString().substring((int) (Math.random() % j)));
                }

            }
            System.out.println(Thread.currentThread().getName()  + "finish task");
        }
    }
}
