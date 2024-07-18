package org.lkg.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.lkg.request.CommonIntResp;
import org.lkg.request.InternalResponse;
import org.lkg.simple.FileUtil;
import org.lkg.up_download.down.DownFileUtil;
import org.lkg.up_download.down.DownloadService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    @ResponseBody
    public boolean testDownload(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        InternalResponse internalResponse = new InternalResponse(null);
        internalResponse.setResult("jhhh");

        List<InternalResponse> list = new ArrayList<>();
        list.add(internalResponse);

        downloadService.downloadWithExcel(list, new String[]{"响应结果", "异常列表", "状态码", "请求列表", "耗时"}, "响应数据", httpServletResponse);
        return true;
    }

    // TODO 合并下载


    @GetMapping("/secret-download")
    // 有没有无所谓
    @ResponseBody
    public void secretDownload(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        URL i1 = this.getClass().getClassLoader().getResource("office" + File.separator + "tes3t.xls");
        URL i2 = this.getClass().getClassLoader().getResource("office" + File.separator + "test.xls");
        File f1 = new File(i1.getPath());
        File f2 = new File(i2.getPath());
        ArrayList<File> files = Lists.newArrayList(f1, f2);

        downloadService.downloadWithZip(httpServletResponse, "test", files);
    }


    @GetMapping("/url-download")
    public void urlDownload(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        Set<String> files = Sets.newHashSet(
                "https://img2024.cnblogs.com/blog/35695/202407/35695-20240713070336838-1837943664.jpg",
                "https://img.pngsucai.com/00/84/18/c788c47b4da7c6f8.webp",
                "https://m.jjjjxs.com/e/DownSys/doaction.php?enews=DownSoft&classid=9&id=49094&pathid=0&pass=ee247a67a5adcf1dfb1abecbd1ff5635&p=:::");

        try {
            DownFileUtil.downLoadZipWithUrlList(httpServletResponse, files, "lkg" );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
