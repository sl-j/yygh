package com.lei.yygh.msm.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {
    @Value("${aliyun.sms.regionId}")
    private String regionId;

    @Value("${aliyun.sms.accessKeyID}")
    private String accessKeyID;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    public static String REGION_ID;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;

    @Override
    public void afterPropertiesSet() throws Exception {
        REGION_ID = regionId;
        ACCESS_KEY_ID = accessKeyID;
        ACCESS_KEY_SECRET = accessKeySecret;
    }
}
