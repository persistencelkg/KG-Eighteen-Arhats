package org.lkg.test;

import org.lkg.request.InternalResponse;
import org.lkg.simple.FileUtil;
import org.lkg.up_download.down.DownloadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/9 7:26 PM
 */
@Controller
//@RequestMapping("/test")
public class TestController {

    @Resource
    private DownloadService downloadService;

    @GetMapping("/")
    public String requestHtml() {
        return "test-upload";
    }

    @PostMapping("/upload")
    public String upLoad(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }
        try {
            // 保存文件到指定目录
            File dest = new File(FileUtil.getCurrentResourceDir(this, "off") + File.separator + file.getOriginalFilename());
            file.transferTo(dest);
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'.");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file '" + file.getOriginalFilename() + "'.");
        }
        // 回到当前页，告诉结果
        return "redirect:/";
    }


    @GetMapping("/download")
    public boolean testDownload(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        InternalResponse internalResponse = new InternalResponse(null);
        internalResponse.setResult("jhhh");

        List<InternalResponse> list = new ArrayList<>();
        list.add(internalResponse);

        downloadService.download(list, new String[]{"响应结果", "异常列表", "状态码", "请求列表", "耗时"}, "响应数据", httpServletResponse);
        return true;
    }
}
