package com.lei.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.model.order.OrderInfo;

import java.util.Map;

public interface WeixinService {
    Map createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId);

    /**
     * 微信退款操作
     * @param orderId
     * @return
     */
    Boolean refund(Long orderId);
}
