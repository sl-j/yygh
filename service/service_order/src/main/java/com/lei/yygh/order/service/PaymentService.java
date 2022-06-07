package com.lei.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.model.order.OrderInfo;
import com.lei.yygh.model.order.PaymentInfo;

import java.util.Map;

public interface PaymentService extends IService<PaymentInfo> {
    void savePaymentInfo(OrderInfo orderInfo, Integer status);

    void paySuccess(String out_trade_no, Map<String, String> resultMap);

    /**
     * 根据订单id和支付类型获取支付信息
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId,Integer paymentType);
}
