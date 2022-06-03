package com.lei.yygh.msm.service.impl;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.lei.yygh.msm.service.MSMService;
import com.lei.yygh.msm.util.ConstantPropertiesUtils;
import com.lei.yygh.vo.msm.MsmVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MSMServiceImpl implements MSMService {
    @Override
    public boolean send(String phone, String code) {
        if(StringUtils.isEmpty(phone)){
            return false;
        }

        //整合阿里云短信服务
        com.aliyun.dysmsapi20170525.Client client = null;
        try {
            client = createClient();
        if(code == null){
            code = "123456";
        }
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("阿里云短信测试")
                .setTemplateCode("SMS_154950909")
                .setPhoneNumbers("15713824706")
                .setTemplateParam("{\"code\":"+code+"}");
        RuntimeOptions runtime = new RuntimeOptions();

        // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
            SendSmsResponseBody body = sendSmsResponse.getBody();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean send(MsmVo msmVo) {
        if(!StringUtils.isEmpty(msmVo.getPhone())){
            String code = (String) msmVo.getParam().get("code");
            boolean isSend = send(msmVo.getPhone(), code);
            return isSend;
        }
        return false;
    }

    public static com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(ConstantPropertiesUtils.ACCESS_KEY_ID)
                // 您的AccessKey Secret
                .setAccessKeySecret(ConstantPropertiesUtils.ACCESS_KEY_SECRET);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }
}
