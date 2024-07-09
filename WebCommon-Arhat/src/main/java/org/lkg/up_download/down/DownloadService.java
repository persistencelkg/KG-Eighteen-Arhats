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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;


public interface DownloadService {

    Logger log = LoggerFactory.getLogger(DownloadService.class.getSimpleName());

    // db list -> input stream -> workbook -> output stream -> response
    CommonResp<Boolean, Integer> download(Collection<?> list, String[] head, String fileName, HttpServletResponse response);


}
