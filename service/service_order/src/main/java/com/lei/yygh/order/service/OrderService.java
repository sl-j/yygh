package com.lei.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.model.order.OrderInfo;
import com.lei.yygh.model.user.UserInfo;
import com.lei.yygh.vo.order.OrderQueryVo;
import org.springframework.stereotype.Service;

public interface OrderService extends IService<OrderInfo> {
    Long submitOrder(String scheduleId, Long patientId);

    void patientTips();

    OrderInfo getOrders(String orderId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    /**
     * 取消预约
     * @param orderId
     * @return
     */
    Boolean cancelOrder(Long orderId);
}
