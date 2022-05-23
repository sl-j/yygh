package com.lei.yygh.user.service;

import com.lei.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService {
    Map<String, Object> login(LoginVo loginVo);
}
