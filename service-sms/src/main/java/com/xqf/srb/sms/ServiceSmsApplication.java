package com.xqf.srb.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Xqf
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan({"com.xqf.srb", "com.xqf.common"})
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceSmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSmsApplication. class,args);
    }
}
