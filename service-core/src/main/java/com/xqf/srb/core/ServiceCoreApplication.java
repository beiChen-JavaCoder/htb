package com.xqf.srb.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan({"com.xqf.srb","com.xqf.common"})
@EnableSwagger2
@EnableDiscoveryClient
public class ServiceCoreApplication {

    public static void main(String[] args){
            SpringApplication.run(ServiceCoreApplication.class, args);
    }
}