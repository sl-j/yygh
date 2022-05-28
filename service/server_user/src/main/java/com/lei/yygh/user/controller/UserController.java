package com.lei.yygh.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.model.user.UserInfo;
import com.lei.yygh.user.service.UserInfoService;
import com.lei.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.List;
import java.util.Map;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;


    //用户列表查询
    @ApiOperation(value = "查询用户列表")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> userInfoList = userInfoService.queryList(page,limit,userInfoQueryVo);
        return Result.ok(userInfoList);
    }

    //用户锁定
    @ApiOperation(value = "用户锁定")
    @GetMapping("lock/{userId}/{status}")
    public Result lock(@PathVariable Long userId,
                       @PathVariable Integer status){
        userInfoService.lock(userId,status);
        return Result.ok();
    }

    //用户详情信息
    @ApiOperation(value = "用户详情")
    @GetMapping("show/{userId}")
    public Result show(@PathVariable Long userId){
        Map<String,Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }

    //认证审批
    @ApiOperation(value = "用户认证审批")
    @GetMapping("approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId,
                           @PathVariable Integer authStatus){
        userInfoService.approval(userId,authStatus);
        return Result.ok();
    }
}
