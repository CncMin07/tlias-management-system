package com.fallenleaves.controller;

import com.fallenleaves.pojo.Result;
import com.fallenleaves.utils.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;



@RestController
@Slf4j
public class UploadController {

    @Autowired
    public AliyunOSSOperator aliyunOSSOperator;


    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws Exception {
        log.info("文件上传："+file.getOriginalFilename());
        //将文件交给oss存储管理
        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());
        log.info("文件上传的oss,rul:{}",url);
        return Result.success(url);
    }





//    @PostMapping("/upload")
//    public Result upload(String name , Integer age, MultipartFile file) throws IOException {
//        log.info("接受参数：{},{},{}",name,age,file);
//        //获取文件名
//        String originalFilename=file.getOriginalFilename();
//        //获取文件后缀名
//        String extion=originalFilename.substring(originalFilename.lastIndexOf("."));
//        String newName= UUID.randomUUID()+extion;
////        String lastname=file.getContentType();
////        System.out.println(lastname);
////        System.out.println("**************************************");
//        //保存文件
//        file.transferTo(new File( "C:\\AAAA\\idea_tlias_images\\"+ newName));
//        return Result.success();
//    }


}
