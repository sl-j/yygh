package com.lei.yygh.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.model.user.UserInfo;
import com.lei.yygh.vo.user.LoginVo;
import com.lei.yygh.vo.user.UserAuthVo;
import com.lei.yygh.vo.user.UserInfoQueryVo;

import java.awt.*;
import java.util.List;
import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> login(LoginVo loginVo);

    UserInfo selectInfoByOpenId(String openid);

    void userAuth(Long userId, UserAuthVo userAuthVo);

    Page<UserInfo> queryList(Long page, Long limit, UserInfoQueryVo userInfoQueryVo);

    void lock(Long userId, Integer status);

    Map<String, Object> show(Long userId);

    void approval(Long userId, Integer authStatus);
}
