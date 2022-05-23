package com.lei.yygh.user.controller;


import com.lei.yygh.common.result.Result;
import com.lei.yygh.user.service.UserInfoService;
import com.lei.yygh.vo.user.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "用户功能接口")
@RestController
@RequestMapping("api/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;


    @ApiOperation(value = "用户手机号登录接口")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String,Object> info = userInfoService.login(loginVo);
        return Result.ok(info);
    }
}
