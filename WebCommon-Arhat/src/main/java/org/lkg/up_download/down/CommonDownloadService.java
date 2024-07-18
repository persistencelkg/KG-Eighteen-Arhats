package org.lkg.up_download.down;

import org.lkg.simple.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
    public void downloadWithExcel(Collection<?> list, String[] head, String fileName, HttpServletResponse response) {
        DownFileUtil.downloadWithSingleExcel(list, head, fileName, response);
    }



    @Override
    public void downloadWithZip(HttpServletResponse response, String password, List<File> list) {
        String fileName = DateTimeUtils.timeConvertToString(LocalDateTime.now(), DateTimeUtils.YYYY_MM_DD_HH_MM_SS_SSS_SEQ);
        String zip = fileName + ".zip";
        DownFileUtil.downLoadZipWithFileList(response, list, zip);
    }

}
