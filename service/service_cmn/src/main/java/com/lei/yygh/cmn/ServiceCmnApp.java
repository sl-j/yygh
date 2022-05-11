package com.lei.yygh.cmn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.lei.yygh.cmn.mapper")
@ComponentScan(basePackages = "com.lei")
@EnableSwagger2
@EnableCaching
public class ServiceCmnApp {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApp.class,args);
    }
}
