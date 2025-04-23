package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.annotations.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

/**
 * Class name: CommonController
 * Package: com.sky.controller.admin
 * Description:
 *
 * @Create: 2025/4/22 23:13
 * @Author: jay
 * @Version: 1.0
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传:{}", file);
        String filePath = null;
        try {
        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取原始文件后缀名  dfdfdf.png
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
       // 构建新文件名称
        String objName = UUID.randomUUID().toString() + extension ;
        filePath = aliOssUtil.upload(file.getBytes(), objName);

        } catch (IOException e) {
            log.info("文件上传失败:{}", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
        return Result.success(filePath);
    }

}

