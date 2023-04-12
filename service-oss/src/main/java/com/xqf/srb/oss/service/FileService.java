package com.xqf.srb.oss.service;

import java.io.InputStream;

/**
 * @author Xqf
 * @version 1.0
 */
public interface FileService {
    /**
     * @param inputStream 源文件流
     * @param module 模块
     * @param fileName 原始文件名
     * @return 文件存储地址
     */
    String upload(InputStream inputStream, String module, String fileName);


    /**
     * 删除oss指定文件
     * @param url 文件路径
     */
    void removeFile(String url);
}
