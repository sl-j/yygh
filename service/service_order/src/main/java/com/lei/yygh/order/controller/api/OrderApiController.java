package com.lei.yygh.order.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.utils.AuthContextHolder;
import com.lei.yygh.enums.OrderStatusEnum;
import com.lei.yygh.model.order.OrderInfo;
import com.lei.yygh.model.user.UserInfo;
import com.lei.yygh.order.service.OrderService;

import com.lei.yygh.vo.order.OrderQueryVo;
import com.lei.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    //根据订单id查询订单详情
    @ApiOperation(value = "根据订单id查询订单详情")
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId){
        OrderInfo orderInfo = orderService.getOrders(orderId);
        return Result.ok(orderInfo);
    }

    @ApiOperation(value = "订单列表（条件查询带分页）")
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       OrderQueryVo orderQueryVo,
                       HttpServletRequest request){
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> iPage = orderService.selectPage(pageParam,orderQueryVo);
        return Result.ok(iPage);
    }


    @ApiOperation(value = "获取订单状态")
    @GetMapping("auth/getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

}
