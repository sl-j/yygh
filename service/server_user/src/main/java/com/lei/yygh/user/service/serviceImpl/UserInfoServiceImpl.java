package com.lei.yygh.user.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lei.yygh.common.exception.YyghException;
import com.lei.yygh.common.help.JwtHelper;
import com.lei.yygh.common.result.ResultCodeEnum;
import com.lei.yygh.model.user.UserInfo;
import com.lei.yygh.user.mapper.UserInfoMapper;
import com.lei.yygh.user.service.UserInfoService;
import com.lei.yygh.vo.user.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //获取手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //判断验证码和手机号是否为空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 判断验证码和输入的验证码是否一致
        String redisCode = redisTemplate.opsForValue().get(phone);
        if(!redisCode.equals(code)){
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        //判断是否是第一次登录
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone,phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);

        if(userInfo == null){//第一次登录
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }

        //判断用户是被否被禁用
        if(userInfo.getStatus() == 0){
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        //不是第一次，直接进行登录

        //返回登录信息：用户名、token
        Map<String,Object> map = new HashMap<>();
        String name = userInfo.getName();

        if(StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }

        if(StringUtils.isEmpty(name)){
            name = userInfo.getPhone();
        }

        map.put("name",name);
        //token生成
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token",token);

        return map;
    }
}
