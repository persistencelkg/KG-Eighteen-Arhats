package org.lkg.up_download.upload;

import org.lkg.request.CommonResp;
import org.lkg.request.InternalResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/8 8:30 PM
 */
public interface CommonUploadService {

    CommonResp<Boolean, Integer> upload(MultipartFile multipartFile);
}
