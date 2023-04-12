package com.xqf.srb.oss.aliyunoss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.boot.test.context.SpringBootTest;

public class OSSTest {

    // Endpoint以杭州为例，其它Region请按实际情况填写。
    String endpoint = "oss-cn-hangzhou.aliyuncs.com";
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    String accessKeyId = "LTAI5tJfj2LLZYzL4BjewCau";
    String accessKeySecret = "WYQ6dFrfAfpkBNnUxPKbJ7F0Iyuw6f";
    String bucketName = "xqf-file-hahaha";

    @Test
    public void testCreateBucket() {
        
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 创建存储空间。
        ossClient.createBucket(bucketName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
    @Test
    public void testAccessControl() {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 设置存储空间的访问权限为：公共读。
        ossClient.deleteBucket(bucketName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}