package com.lei.yygh.user.controller;


import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.utils.AuthContextHolder;
import com.lei.yygh.user.service.UserInfoService;
import com.lei.yygh.vo.user.LoginVo;
import com.lei.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @ApiOperation(value = "用户认证接口")
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        //传入用户id和vo对象
        userInfoService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }

    @ApiOperation(value = "根据用户id获取用户信息")
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        return Result.ok(userInfoService.getById(userId));

    }
}
