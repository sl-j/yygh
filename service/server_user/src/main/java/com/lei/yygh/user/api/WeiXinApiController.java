package com.lei.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.lei.yygh.common.help.JwtHelper;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.model.user.UserInfo;
import com.lei.yygh.user.service.UserInfoService;
import com.lei.yygh.user.utils.ConstantWxPropertiesUtils;
import com.lei.yygh.user.utils.HttpClientUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "微信操作接口")
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeiXinApiController {

    @Autowired
    private UserInfoService userInfoService;

    //1.生成微信扫描二维码
    //返回生成二维码需要的参数
    @ApiOperation(value = "生成微信登录二维码")
    @GetMapping("getLoginParam")
    public String getLoginParam(){
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //回调地址
        String wxOpenRedirectUrl = ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL;
        try {
            wxOpenRedirectUrl =URLEncoder.encode(wxOpenRedirectUrl,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //传递参数
        String wxUrl = String.format(
                baseUrl,
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                wxOpenRedirectUrl,
                "wxatguigu"
        );
        //重定向
        return "redirect:" + wxUrl;
    }

    //微信回调方法
    @ApiOperation(value = "微信回调方法")
    @GetMapping("callback")
    public String callback(String code,String state){
        //获取到临时票据 code


        //通过code得到access_token和openid
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);

        try {
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            JSONObject jsonObject = JSONObject.parseObject(accessTokenInfo);
            String access_token = (String) jsonObject.get("access_token");
            String openid = (String) jsonObject.get("openid");
            UserInfo userInfo1 = null;

            //判断数据库中是否已经存在此微信用户
            UserInfo userInfoExist = userInfoService.selectInfoByOpenId(openid);
            if(userInfoExist == null) {
                //通过access_token和openid访问微信接口，获取扫码人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String userInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println(userInfo);
                JSONObject resultUserInfoJson = JSONObject.parseObject(userInfo);
                //解析用户信息
                //用户昵称
                String nickname = resultUserInfoJson.getString("nickname");
                //用户头像
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                //将扫码人信息添加到数据库中
                userInfo1 = new UserInfo();
                userInfo1.setOpenid(openid);
                userInfo1.setNickName(nickname);
                userInfo1.setStatus(1);
                userInfoService.save(userInfo1);
            }

            //返回name和token字符串
            Map<String,String> map = new HashMap<>();

            String name = userInfoExist.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfoExist.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfoExist.getPhone();
            }
            map.put("name", name);

            //判断userInfo是否有手机号，如果手机号为空，返回openid
            //如果手机号不为空，返回openid值是空字符串
            //前端判断：如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机号
            if(StringUtils.isEmpty(userInfoExist.getPhone())) {
                map.put("openid", userInfoExist.getOpenid());
            } else {
                map.put("openid", "");
            }
            //使用jwt生成token字符串
            String token = JwtHelper.createToken(userInfoExist.getId(), name);
            map.put("token", token);
            //跳转到前端页面
            return "redirect:http://blog.xiaoshit.com/";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
