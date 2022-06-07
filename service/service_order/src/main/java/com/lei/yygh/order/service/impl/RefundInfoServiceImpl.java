package com.lei.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lei.yygh.enums.RefundStatusEnum;
import com.lei.yygh.model.order.PaymentInfo;
import com.lei.yygh.model.order.RefundInfo;
import com.lei.yygh.order.mapper.PaymentMapper;
import com.lei.yygh.order.mapper.RefundInfoMapper;
import com.lei.yygh.order.service.PaymentService;
import com.lei.yygh.order.service.RefundInfoService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
    /**
     * 保存退款记录
     *
     * @param paymentInfo
     * @return
     */
    @Override
    public RefundInfo savaRefundInfo(PaymentInfo paymentInfo) {
        //判断是否有重复数据
        LambdaQueryWrapper<RefundInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundInfo::getOrderId,paymentInfo.getOrderId());
        queryWrapper.eq(RefundInfo::getPaymentType,paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(queryWrapper);

        if(refundInfo != null){//有相同数据
            return refundInfo;
        }

        //添加记录
        refundInfo = new RefundInfo();
        refundInfo.setCreateTime(new Date());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        refundInfo.setSubject(paymentInfo.getSubject());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
