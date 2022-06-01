package com.lei.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.model.order.OrderInfo;
import org.springframework.stereotype.Service;

@Service
public interface OrderService extends IService<OrderInfo> {
    Long submitOrder(String scheduleId, Long patientId);
}
