package com.lei.yygh.msm.controller;

import com.lei.yygh.common.result.Result;
import com.lei.yygh.msm.service.MSMService;
import com.lei.yygh.msm.util.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Api(tags = "阿里云短信接口")
@RestController
@RequestMapping("/api/msm")
public class MSMController {

    @Autowired
    private MSMService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    
    //发送手机验证码
    @ApiOperation(value = "发送短信接口")
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone){
        //从redis获取验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) return Result.ok();

        //如果从redis获取不到，生成验证码
        code = RandomUtil.getSixBitRandom();
        //调用阿里云进行短信发送服务
        boolean isSend = msmService.send(phone,code);
        if(isSend){
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return Result.ok();
        }else{
            return Result.fail().message("发送短信失败");
        }
    }
}
