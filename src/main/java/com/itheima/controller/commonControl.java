package com.itheima.controller;

import com.itheima.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class commonControl {

    @Value("${regi.path}")
    private String basepath;


    /**
     *
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file)
    {
        log.info("上传文件 {}",file);
        //1.动态生成新的文件名,但还缺少后缀
        String randomUUID = UUID.randomUUID().toString();

        //2.给新文件名设置后缀
        //2.1 获得原文件的后缀类型
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));

        //此时，才是一个完整的文件名
        String name=randomUUID+suffix;

        System.out.println("完整文件名:"+name);

        //接下来，要判断将要生成的文件，所在的目录是否存在
        File file1 = new File(basepath);
        if(!file1.exists())
        {
            //如果不存在，就自己创建个目录
            file1.mkdirs();
        }

        try {
            file.transferTo(new File(basepath+name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(name);
    }

    /**
     * 文件下载
     * @param response
     * @param name
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response,String name)
    {
        try {
            //文件输入流
            FileInputStream inputStream = new FileInputStream(basepath + name);

            //获得输出流
            ServletOutputStream outputStream = response.getOutputStream();

            byte[] bytes = new byte[1024];
            int len=0;
            //当len=-1时，表示文件已被读完
            while ((len=inputStream.read(bytes))!=-1)
            {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //释放资源
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
