package com.itheima.reggie.web.controller;

import com.itheima.reggie.utils.FileUtil;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author Vsunks.v
 * @Date 2022/6/11 15:31
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description: 处理文件上传下载的控制器
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {


    @Value("${reggie.path}")
    private String basePath;


    /**
     * 处理上传请求
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {

        // 接受上传数据

        // 获取原始文件名，获取其真是的后缀（可以不用）
        String ofn = file.getOriginalFilename();
        int dotIndex = ofn.lastIndexOf(".");
        String suffixName = ofn.substring(dotIndex);

        // 包含三级目录的文件名
        String fileNameWithPath = FileUtil.getFileNameWithPath();

        // 创建文件
        FileUtil.makeDirs(fileNameWithPath, basePath);

        // 保存到服务硬盘中某个地方（图片运存服务器（对象服务器OSS））
        //d:/img/xxx.jpg
        file.transferTo(new File(basePath + fileNameWithPath + suffixName));

        //给出响应
        return R.success("文件上传成功", fileNameWithPath + suffixName);

    }


    /**
     * 图片下载。如果图片不存在，则不响应任何内容
     * @param name 包含随机三级路径的文件名
     * @param response
     * @throws IOException
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {

        //1. 定义输入流，关联拼接后的对应文件。如果图片文件不存在，就直接结束请求
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(new File(basePath,
                    name)));
        } catch (FileNotFoundException e) {
            log.warn("图片文件找不到");
            return;
        }
        //2. 通过`response`对象，获取到输出流
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
        } catch (IOException e) {
            log.warn("响应图片失败");
            e.printStackTrace();
        }

        //3. 通过`response`对象设置响应数据格式(`image/jpeg`)
        response.setContentType("image/jpeg");

        //4. 通过输入流读取文件数据，然后通过上述的输出流写回浏览器
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = bis.read(bytes)) > 0) {
            os.write(bytes, 0, len);
            os.flush();
        }
        //5. 关闭资源
        bis.close();
    }

}
