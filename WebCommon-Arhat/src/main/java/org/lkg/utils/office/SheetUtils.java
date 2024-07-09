package org.lkg.utils.office;

import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.lkg.request.InternalResponse;
import org.lkg.simple.IOStreamUtil;
import org.lkg.simple.ObjectUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/9 11:08 AM
 */
public class SheetUtils {

    private static final int HEAD_TRACK_CELL = 99;


    /**
     * 目前无法暂不支持按具体类型解析，所以需要统一转换成字段类型为 string 的对象
     *
     * @param tClass
     * @param inputStream
     * @return 返回字段为string 类型的对象
     */
    public static <T> List<T> iteratorSheetToJavaObj(Class<T> tClass, InputStream inputStream) {
        return iteratorSheetToJavaObj(tClass, ExcelUtils.createWorkBookFromInputStream(inputStream));

    }

    public static <T> List<T> iteratorSheetToJavaObj(Class<T> tClass, Workbook workBookFromInputStream) {
        // TODO check string field
        Sheet sheetAt = workBookFromInputStream.getSheetAt(0);
        Iterator<Row> iterator = sheetAt.iterator();
        List<T> list = new ArrayList<>();
        Cell HEAD_CELL = sheetAt.getRow(0).getCell(HEAD_TRACK_CELL);
        boolean hasJumpHeader = Objects.nonNull(HEAD_CELL) && HEAD_CELL.getBooleanCellValue();
        if (hasJumpHeader) {
            iterator.next();
        }
        while (iterator.hasNext()) {
            Row next = iterator.next();
            Field[] declaredFields = tClass.getDeclaredFields();
            int index = 0;

            T obj;
            try {
                obj = tClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            for (Field declaredField : declaredFields) {
                if (!Modifier.isStatic(declaredField.getModifiers())
                        && (Modifier.isPrivate(declaredField.getModifiers()) || Modifier.isProtected(declaredField.getModifiers()))) {
                    declaredField.setAccessible(true);
                    Cell cell = next.getCell(index++);
                    String stringCellValue = cell.getStringCellValue();
                    try {
                        Method set = tClass.getMethod("set" + ObjectUtil.firstLetterUpper(declaredField.getName()), String.class);
                        set.invoke(obj, stringCellValue);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            list.add(obj);
        }
        IOStreamUtil.close(workBookFromInputStream);
        return list;
    }

    public static List<String> iteratorFirstSheetWithRowIndex(InputStream inputStream, int index) {
        Workbook workBookFromInputStream = ExcelUtils.createWorkBookFromInputStream(inputStream);
        Sheet sheetAt = workBookFromInputStream.getSheetAt(index);
        Iterator<Row> iterator = sheetAt.iterator();
        ArrayList<String> result = new ArrayList<>();
        while (iterator.hasNext()) {
            Row next = iterator.next();
            Cell cell = next.getCell(index);
            String stringCellValue = cell.getStringCellValue();
            if (!ObjectUtil.isEmpty(stringCellValue)) {
                result.add(stringCellValue);
            }
        }
        IOStreamUtil.close(workBookFromInputStream, inputStream);
        return result;
    }


    /**
     * 建议将所有对象的字段都转换成string，因为大部分我们来自db的数据都需要进行转换
     * 所以这么做是必要的，而且内部也会自动将所有类型映射成string，解析之后也是string
     *
     * @param workbook
     * @param list
     * @param head
     */
    public static void batchSaveRow(Workbook workbook, Collection<?> list, String... head) {
        Sheet sheetAt = workbook.getSheetAt(0);
        batchSaveRow(sheetAt, list, head);
        IOStreamUtil.close(workbook);
    }

    public static void batchSaveRow(Sheet sheet, Collection<?> list, String... head) {
        int index = 0;
        if (!ObjectUtil.isEmpty(head)) {
            Row row = sheet.createRow(0);
            for (int i = 0; i < head.length; i++) {
                row.createCell(i).setCellValue(head[i]);
            }
            index = 1;
            row.createCell(HEAD_TRACK_CELL, CellType.BOOLEAN).setCellValue(true);
            sheet.setColumnHidden(HEAD_TRACK_CELL, true);
        }
        batchSaveRow(sheet, index, list);
    }

    public static void batchSaveRow(Sheet sheet, Collection<?> list) {
        batchSaveRow(sheet, 0, list);
    }

    public static void batchSaveRow(Sheet sheet, int rowIndex, Collection<?> list) {
        if (ObjectUtil.isEmpty(list)) {
            return;
        }
        AtomicInteger atomic = new AtomicInteger(rowIndex);
        list.forEach(ref -> createStringCellWithObj(sheet.createRow(atomic.get()), ref));
        // sheet.autoSizeColumn(index++); 影响性能谨慎使用
    }

    public static void createStringCellWithObj(Row row, Object object) {
        Class<?> aClass = object.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        int index = 0;
        for (Field declaredField : declaredFields) {
            try {
                if (!Modifier.isStatic(declaredField.getModifiers())
                        && (Modifier.isPrivate(declaredField.getModifiers()) || Modifier.isProtected(declaredField.getModifiers()))) {
                    declaredField.setAccessible(true);
                    Method get = aClass.getMethod("get" + ObjectUtil.firstLetterUpper(declaredField.getName()));
                    Object invoke = get.invoke(object);
                    if (Objects.isNull(invoke)) {
                        invoke = "";
                    }
                    createStringCellWithDefault(row, index++, String.valueOf(invoke), "");
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("字段解析错误: " + e);
            }
        }
    }

    public static void createStringCellWithDefault(Row row, int index, String value, String defaultValue) {
        Cell cell = row.createCell(index, ObjectUtil.isEmpty(defaultValue) ? CellType.BLANK : CellType.STRING);
        cell.setCellValue(ObjectUtil.isEmpty(value) ? defaultValue : value);
    }

    public static String getCellValue(Cell xssfCell) {
        if (xssfCell.getCellType() == CellType.STRING) {
            return xssfCell.getStringCellValue();
        }
        if (xssfCell.getCellType() == CellType.BLANK) {
            return "";
        }
        if (xssfCell.getCellType() == CellType.NUMERIC) {
            return NumberToTextConverter.toText(xssfCell.getNumericCellValue());
        }
        return xssfCell.getStringCellValue();
    }

    public static void main(String[] args) {
        InternalResponse internalResponse = new InternalResponse(null);
        internalResponse.setResult("jhhh");

        List<InternalResponse> list = new ArrayList<>();
        list.add(internalResponse);

        // 写
        Workbook emptyWorkBook = ExcelUtils.createEmptyWorkBook();
        batchSaveRow(emptyWorkBook, list, "响应结果", "异常列表", "状态码", "请求列表", "耗时");
        try {
            emptyWorkBook.write(ExcelUtils.getDefaultOutputStream("tes3t.xls"));
            emptyWorkBook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-----读取工作簿开始----");
        // 读取
        try {
            List<CustomInternalResponse> ts = iteratorSheetToJavaObj(CustomInternalResponse.class, ExcelUtils.createWorkBookWithLocal("tes3t.xls"));
            System.out.println(ts);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    public static class CustomInternalResponse {
        private String result;

        private String exceptionList;

        private String statusCode;

        private String internalRequest;

        private String costTime;
    }
}
