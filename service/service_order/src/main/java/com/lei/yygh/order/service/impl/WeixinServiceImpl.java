package com.lei.yygh.order.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.lei.yygh.enums.PaymentStatusEnum;
import com.lei.yygh.enums.PaymentTypeEnum;
import com.lei.yygh.model.order.OrderInfo;
import com.lei.yygh.order.service.OrderService;
import com.lei.yygh.order.service.PaymentService;
import com.lei.yygh.order.service.WeixinService;
import com.lei.yygh.order.utils.ConstantPropertiesUtils;
import com.lei.yygh.order.utils.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeixinServiceImpl implements WeixinService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedisTemplate redisTemplate;

    //生成微信支付二维码
    @Override
    public Map createNative(Long orderId) {
        Map payMap = (Map)redisTemplate.opsForValue().get(orderId.toString());
        if(payMap != null){
            return payMap;
        }
        //根据oderId获取订单信息
        OrderInfo orderInfo = orderService.getById(orderId);
        //向支付记录表添加数据
        paymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
        //设置参数，调用微信生成二维码接口
        //把参数转换xml格式，使用商户key进行加密
        Map paramMap = new HashMap();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        String body = orderInfo.getReserveDate() + "就诊"+ orderInfo.getDepname();
        paramMap.put("body", body);
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
        //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
        paramMap.put("total_fee", "1"); //为了测试，统一写成这个值
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
        paramMap.put("trade_type", "NATIVE");

        //调用微信生成二维码
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        //设置参数
        try {
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();
            //返回相关数据
            String xml = client.getContent();
            //xml转换map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println("resultMap:" + resultMap);
            //6 封装返回结果集
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", orderInfo.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url")); //二维码地址

            if(resultMap.get("result_code") != null) {
                redisTemplate.opsForValue().set(orderId.toString(),map,120, TimeUnit.MINUTES);
            }

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //查询支付状态（前端使用的定时器，不断试探）
    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try{
            //根据订单id获取订单信息
            OrderInfo orderInfo = orderService.getById(orderId);

            //封装提交参数
            Map paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

            //设置请求内容
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            //得到微信接口返回数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println("支付状态："+ resultMap);

            return resultMap;
        }catch (Exception e) {
            return null;
        }
    }
}
