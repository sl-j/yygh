package com.lei.yygh.user.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lei.yygh.common.exception.YyghException;
import com.lei.yygh.common.help.JwtHelper;
import com.lei.yygh.common.result.ResultCodeEnum;
import com.lei.yygh.enums.AuthStatusEnum;
import com.lei.yygh.model.user.Patient;
import com.lei.yygh.model.user.UserInfo;
import com.lei.yygh.user.mapper.UserInfoMapper;
import com.lei.yygh.user.service.PatientService;
import com.lei.yygh.user.service.UserInfoService;
import com.lei.yygh.vo.user.LoginVo;
import com.lei.yygh.vo.user.UserAuthVo;
import com.lei.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private PatientService patientService;
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

        UserInfo userInfo = null;
        //如果微信登录，根据openid判断数据库中是否已经存在此微信用户
        if(!StringUtils.isEmpty(loginVo.getOpenid())){
            userInfo = selectInfoByOpenId(loginVo.getOpenid());
            //查询手机号是否已经注册过
            LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfo::getPhone,phone);
            UserInfo userInfoPhone = baseMapper.selectOne(queryWrapper);
            if(userInfo != null){
                //如果手机号已经注册过,进行更新
                if(userInfoPhone != null){
                    userInfoPhone.setOpenid(userInfo.getOpenid());
                    userInfoPhone.setNickName(userInfo.getNickName());
                    userInfoPhone.setStatus(userInfo.getStatus());
                    updateById(userInfoPhone);
                    //删除原先的微信注册信息
                    baseMapper.deleteById(userInfo.getId());
                }else{//如果手机号没有被注册过，插入手机号
                    userInfo.setPhone(phone);
                    updateById(userInfo);
                }
            }else{//微信登录在前，已经在数据库中存储了微信用户信息，所以用户信息不可能为null，如果为null，抛出异常
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }

        }
        //没有使用微信登录的话，userinfo为空,直接进行到这一步
        if(userInfo == null){
            //判断是否是第一次登录
            LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfo::getPhone,phone);
            userInfo = baseMapper.selectOne(queryWrapper);

            if(userInfo == null){//第一次登录
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
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

    @Override
    public UserInfo selectInfoByOpenId(String openid) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getOpenid,openid);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //进行信息更新
        baseMapper.updateById(userInfo);
    }


    //用户列表查询(条件查询带分页)
    @Override
    public Page<UserInfo> queryList(Long page, Long limit, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParam = new Page<>(page, limit);
        //获取条件值
        String name = userInfoQueryVo.getKeyword();//用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus();//认证状态
        //获取开始时间和结束时间
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();

        //非空判断
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(name)){
            queryWrapper.like(UserInfo::getName,name);
        }
        if(!StringUtils.isEmpty(status)){
            queryWrapper.eq(UserInfo::getStatus,status);
        }
        if(!StringUtils.isEmpty(authStatus)){
            queryWrapper.eq(UserInfo::getAuthStatus,authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            queryWrapper.ge(UserInfo::getCreateTime,createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.le(UserInfo::getCreateTime,createTimeEnd);
        }

        Page<UserInfo> userInfoPage = baseMapper.selectPage(pageParam, queryWrapper);
        //编号转化成对应的值
        userInfoPage.getRecords().stream().forEach(item -> {
            packageUserInfo(item);
        });
        return userInfoPage;
    }

    //用户锁定
    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String, Object> result= new HashMap<>();
        //查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo = packageUserInfo(userInfo);
        result.put("userInfo",userInfo);

        //查询就诊人信息
        List<Patient> patients = patientService.findAll(userId);
        result.put("patientList",patients);
        return result;
    }

    //用户认证审批  2 通过 -1 不通过
    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus.intValue() == 2 || authStatus.intValue() == -1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    //编号转化成对应的值
    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理用户认证状态
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态
        String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
        return userInfo;
    }
}
