package com.lei.yygh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.lei.yygh.hosp.mapper")
@ComponentScan(basePackages = "com.lei")
@EnableSwagger2
public class ServerUser {
    public static void main(String[] args) {
        SpringApplication.run(ServerUser.class,args);
    }
}
