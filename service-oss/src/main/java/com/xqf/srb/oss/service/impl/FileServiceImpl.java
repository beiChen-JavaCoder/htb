package com.xqf.srb.oss.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.xqf.common.exception.BusinessException;
import com.xqf.common.result.ResponseEnum;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.oss.service.FileService;
import com.xqf.srb.oss.util.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author Xqf
 * @version 1.0
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Override
    public String upload(InputStream inputStream, String module, String fileName) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = OssProperties.ENDPOINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = OssProperties.KEY_ID;
        String accessKeySecret = OssProperties.KEY_SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = OssProperties.BUCKET_NAME;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        if (!ossClient.doesBucketExist(bucketName)) {
            //如果不存在bucket_name则生成对应的
            ossClient.createBucket(bucketName);
            //设置oss实例的访问权限：公共读
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }
        //构建日期路径：avatar/2019/02/26/文件名
        String prefix = new DateTime().toString("yyyy/MM/dd");
        UUID uuid = UUID.randomUUID();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String key = "avatar" + "/"+module+"/" + prefix + "/" + uuid + suffix;
        //文件上传至阿里云
        try {
            ossClient.putObject(bucketName, key, inputStream);
        } catch (Exception e) {
            log.error("阿里云上传文件失败，请检查配置");
        }
        // 关闭OSSClient。
        ossClient.shutdown();

        //返回给前端文件url地址
        return "https://" + bucketName + "." + OssProperties.ENDPOINT + "/" + key;


    }

    @Override
    public void removeFile(String url) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                OssProperties.ENDPOINT,
                OssProperties.KEY_ID,
                OssProperties.KEY_SECRET);

        //      https://xqf-srb-file.oss-cn-hangzhou.aliyuncs.com/avatar/%E5%A4%B4%E5%83%8F/2023/01/13/f61f0b01-c98a-4768-aab6-cffe6edab3fe.png
        //文件名（服务器上的文件路径）
        String host = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/";
        String objectName = url.substring(host.length());

        // 删除文件。
        ossClient.deleteObject(OssProperties.BUCKET_NAME, objectName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
