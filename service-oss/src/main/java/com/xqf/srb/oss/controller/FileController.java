package com.xqf.srb.oss.controller;

import com.xqf.common.exception.BusinessException;
import com.xqf.common.result.ResponseEnum;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Xqf
 * @version 1.0
 */
@RequestMapping("/api/oss/file")
@RestController
@Api(tags = "阿里云文件管理")
//@CrossOrigin
@Slf4j
public class FileController {

    @Resource
    FileService fileService;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public ResultResponse upload(
            @ApiParam(value = "源文件", required = true)
            @RequestParam("file") MultipartFile file,
            @ApiParam(value = "模块", required = true)
            @RequestParam("module") String module) {
        String fileName = null;

            fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            if (suffix.equals(".png")||suffix.equals(".jpg")){

        try {
            InputStream inputStream = file.getInputStream();
            String url = fileService.upload(inputStream, module, fileName);
            return ResultResponse.ok().message("文件上传成功").data("url", url);
        } catch (IOException e) {
            //文件上传失败抛出异常
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }

    }
        throw new BusinessException(ResponseEnum.FILE_TYPE_NOT_SUPPORTED);
    }
    @ApiOperation("删除OSS文件")
    @DeleteMapping("/remove")
    public ResultResponse remove(
            @ApiParam(value = "要删除的文件路径", required = true)
            @RequestParam("url") String url) {
        fileService.removeFile(url);
        return ResultResponse.ok().message("删除成功");
    }
}
