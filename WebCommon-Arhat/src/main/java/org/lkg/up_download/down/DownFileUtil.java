package org.lkg.up_download.down;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.lkg.request.InternalRequest;
import org.lkg.request.InternalResponse;
import org.lkg.request.SimpleRequestUtil;
import org.lkg.simple.IOStreamUtil;
import org.lkg.simple.ObjectUtil;
import org.lkg.simple.UrlUtil;
import org.lkg.up_download.ContentTypeEnum;
import org.lkg.utils.office.ExcelUtils;
import org.lkg.utils.office.SheetUtils;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/15 8:54 PM
 */
@Slf4j
public class DownFileUtil {

    public static void downloadWithSingleExcel(Collection<?> list, String fileName, HttpServletResponse response) {
        downloadWithSingleExcel(list, null, fileName, response);
    }

    public static void downloadWithSingleExcel(Collection<?> list, String[] head, String fileName, HttpServletResponse response) {
        Workbook emptyWorkBook = null;
        try {
            emptyWorkBook = ExcelUtils.createEmptyWorkBook();
            SheetUtils.batchSaveRow(emptyWorkBook, list, head);
            populateResponse(response, ContentTypeEnum.EXCEL, fileName);
            emptyWorkBook.write(response.getOutputStream());
            log.info("{}下载完成", fileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            IOStreamUtil.close(emptyWorkBook);
        }
    }

    public static void downLoadZipWithFileList(HttpServletResponse response, List<File> list, String zip) {
        ZipOutputStream zipOutputStream = null;
        long total = 0;
        try {
            populateResponse(response, ContentTypeEnum.ZIP, zip);
            zipOutputStream = new ZipOutputStream(response.getOutputStream());
            for (File ref : list) {
                ZipEntry zipEntry = new ZipEntry(ref.getName());
                zipOutputStream.putNextEntry(zipEntry);
                long copy = IOStreamUtil.copy(ref, zipOutputStream, true);
                log.info("file:{} read size:{} KB", ref.getName(), copy / 1024);
                zipOutputStream.closeEntry();
                total += copy;
            }
            if (ObjectUtil.isEmpty(list)) {
                zipOutputStream.closeEntry();
            }
            log.info("{} has down load zip size:{}KB", zip, total / 1024);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IOStreamUtil.close(zipOutputStream);
            File file = new File(zip);
            if (file.exists()) {
                boolean delete = file.delete();
                log.info("delete file：{}", zip);
            }
        }
    }

    private static void populateResponse(HttpServletResponse response, ContentTypeEnum contentTypeEnum, String zip) {
        response.reset();
        response.setContentType(contentTypeEnum.getContentTypeValue());
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + UrlUtil.encodeUrl(contentTypeEnum.exchangeName(zip)));
    }


    public static void downLoadZipWithUrlList(HttpServletResponse response, Set<String> urlList, String zip) throws IOException {
        // 1. 打开url 获取输入流
        // 2. 解析url 文件名
        // 3. 循环写
        long total = 0;
        populateResponse(response, ContentTypeEnum.ZIP, zip);
        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
        for (String url : urlList) {
            InternalResponse request = SimpleRequestUtil.request(InternalRequest.createGetRequest(url));
            String fileName = UrlUtil.parseName(request.getLatestResponseUrl());
            byte[] bytes = request.getResult().getBytes(StandardCharsets.UTF_8);
            log.info("url:{} size:{} KB", fileName, bytes.length / 1024);

            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(request.getResultBytes());
            zipOutputStream.closeEntry();
            total += bytes.length;
        }
        if (ObjectUtil.isEmpty(urlList)) {
            zipOutputStream.closeEntry();
        }
        IOStreamUtil.close(zipOutputStream);
        log.info("{} has down load zip size:{}KB", zip, total / 1024);
    }

    public static void main(String[] args) {
        System.out.println(UrlUtil.encodeUrl(ContentTypeEnum.EXCEL.exchangeName("test")));
        System.out.println(UrlUtil.encodeUrl(ContentTypeEnum.EXCEL.exchangeName("test.zip")));
        System.out.println(Arrays.toString(ImageIO.getReaderFileSuffixes()));
        // crawl https://m.jjjjxs.com/txt/dl-9-49094.html
        //  https://d.jjxswshuku.com/d/file/p/txt/2024/%E3%80%8A%E6%88%91%E7%9A%84%E7%8B%AC%E7%AB%8B%E6%97%A5%E3%80%8B%E4%BD%9C%E8%80%85%EF%BC%9A%E5%AE%B9%E5%85%89.txt
        InternalResponse request = SimpleRequestUtil.request(InternalRequest.createGetRequest(
                "https://img2024.cnblogs.com/blog/35695/202407/35695-20240713070336838-1837943664.jpg"
        , InternalRequest.BodyEnum.HTML, null));
        System.out.println(request);



    }

}
