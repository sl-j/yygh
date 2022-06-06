package com.lei.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lei.yygh.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
}
