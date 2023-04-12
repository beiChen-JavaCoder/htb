package com.xqf.srb.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Xqf
 * @version 1.0
 * swagger文档接口
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket adminApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(adminInfo())
                .groupName("adminapi")
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }
    private ApiInfo adminInfo() {
        return new ApiInfoBuilder()
                .title("后台api文档")
                .description("简介")
                .version("1.0")
                .contact(new Contact("xqf", "http://xqf.com", "xqf@xwf.com"))
                .build();
    }
    @Bean
    public Docket webApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(webInfo())
                .groupName("webapi")
                .select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }
    private ApiInfo webInfo() {
        return new ApiInfoBuilder()
                .title("前台api文档")
                .description("简介")
                .version("1.0")
                .contact(new Contact("xqf", "http://xqf.com", "xqf@xwf.com"))
                .build();
    }
    }
