package com.lei.yygh.user.config;

import org.apache.ibatis.annotations.Mapper;
import org.mapstruct.MapperConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.lei.yygh.user.mapper.UserInfoMapper")
public class UserConfig {
}
