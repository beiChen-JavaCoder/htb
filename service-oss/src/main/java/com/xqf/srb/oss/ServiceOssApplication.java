package com.xqf.srb.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Xqf
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan({"com.xqf.srb","com.xqf.common"})
@EnableDiscoveryClient
public class ServiceOssApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApplication.class);
    }
}