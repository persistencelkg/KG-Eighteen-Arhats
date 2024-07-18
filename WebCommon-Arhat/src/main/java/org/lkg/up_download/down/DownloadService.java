package org.lkg.up_download.down;

import org.lkg.request.CommonIntResp;
import org.lkg.request.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Collection;
import java.util.List;


public interface DownloadService {

    Logger log = LoggerFactory.getLogger(DownloadService.class.getSimpleName());

    // db list -> input stream -> workbook -> output stream -> response
   void downloadWithExcel(Collection<?> list, String[] head, String fileName, HttpServletResponse response);


    void downloadWithZip(HttpServletResponse response, String password, List<File> list);
}
