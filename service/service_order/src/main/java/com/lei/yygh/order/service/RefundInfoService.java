package com.lei.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.model.order.PaymentInfo;
import com.lei.yygh.model.order.RefundInfo;

public interface RefundInfoService extends IService<RefundInfo> {

    /**
     * 保存退款记录
     * @param paymentInfo
     * @return
     */
    RefundInfo savaRefundInfo(PaymentInfo paymentInfo);
}
