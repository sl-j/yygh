package com.lei.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.model.user.UserInfo;
import com.lei.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> login(LoginVo loginVo);

    UserInfo selectInfoByOpenId(String openid);
}
