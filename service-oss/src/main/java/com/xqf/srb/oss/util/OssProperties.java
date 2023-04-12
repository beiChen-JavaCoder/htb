package com.xqf.srb.oss.util;

import javafx.fxml.Initializable;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author Xqf
 * @version 1.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties implements InitializingBean {
    private String endpoint;
    private String keyId;
    private String keySecret;
    private String bucketName;

    public static String ENDPOINT;
    public static String KEY_ID;
    public static String KEY_SECRET;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {

        //当私有成员被赋值后，此方法自动被调用，从而初始化常量
        ENDPOINT = endpoint;
        KEY_ID = keyId;
        KEY_SECRET = keySecret;
        BUCKET_NAME = bucketName;
    }
}
