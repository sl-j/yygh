package com.lei.yygh.order.controller.api;

import com.lei.yygh.common.result.Result;
import com.lei.yygh.hosp.client.HospitalFeignClient;
import com.lei.yygh.model.user.Patient;
import com.lei.yygh.order.service.OrderService;
import com.lei.yygh.user.client.PatientFeignClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "订单功能")
@RestController
@RequestMapping("api/order/orderInfo")
public class OrderApiController {

    @Autowired
    private OrderService orderService;


    //根据就诊人和排班数据生成订单
    @ApiOperation(value = "生成挂号订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result submitOrder(@PathVariable String scheduleId,
                              @PathVariable Long patientId){
        Long orderId = orderService.submitOrder(scheduleId,patientId);
        return Result.ok(orderId);
    }


}
