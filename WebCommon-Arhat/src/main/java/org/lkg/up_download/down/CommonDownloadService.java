package org.lkg.up_download.down;

import org.apache.poi.ss.usermodel.Workbook;
import org.lkg.request.CommonResp;
import org.lkg.simple.IOStreamUtil;
import org.lkg.simple.UrlUtil;
import org.lkg.utils.office.ExcelUtils;
import org.lkg.utils.office.SheetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/9 5:29 PM
 */
@Service
public class CommonDownloadService implements DownloadService {

    Logger log = LoggerFactory.getLogger(CommonDownloadService.class.getSimpleName());

    // db list -> input stream -> workbook -> output stream -> response
    @Override
    public CommonResp<Boolean, Integer> download(Collection<?> list, String[] head, String fileName, HttpServletResponse response) {
        Workbook emptyWorkBook;
        ServletOutputStream outputStream = null;
        try {
            emptyWorkBook = ExcelUtils.createEmptyWorkBook();
            SheetUtils.batchSaveRow(emptyWorkBook, list, head);
            response.reset();
            response.setContentType("application/vnd.ms-excel");
            if (!(fileName.endsWith(".xlsx") || fileName.endsWith(".xls") || fileName.endsWith(".csv"))) {
                fileName += ".xls";
            }
            String encodeFilename = UrlUtil.encodeUrl(fileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + encodeFilename);
            outputStream = response.getOutputStream();
            emptyWorkBook.write(outputStream);
            log.info("{}下载完成", fileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new CommonResp<>();
    }


}
