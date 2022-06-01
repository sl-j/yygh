package com.lei.yygh.hosp.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.hosp.service.DepartmentService;
import com.lei.yygh.hosp.service.HospitalService;
import com.lei.yygh.hosp.service.ScheduleService;
import com.lei.yygh.model.hosp.Hospital;
import com.lei.yygh.model.hosp.Schedule;
import com.lei.yygh.vo.hosp.HospitalQueryVo;
import com.lei.yygh.vo.hosp.ScheduleOrderVo;
import com.lei.yygh.vo.order.SignInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.util.List;
import java.util.Map;

@Api(tags = "前台用户使用接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {

    @Autowired
    private HospitalService hospitalServicel;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        return hospitalServicel.selecthospPage(page,limit,hospitalQueryVo);
    }

    @ApiOperation(value = "根据医院名称查询")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosName(@PathVariable String hosname){
        List<Hospital> hospitals = hospitalServicel.findByHosName(hosname);
        return Result.ok(hospitals);
    }

    @ApiOperation(value = "根据医院编号获取科室信息")
    @GetMapping("department/{hoscode}")
    public Result findDepartment(@PathVariable String hoscode){
        return departmentService.getDeptList(hoscode);
    }

    @ApiOperation(value = "根据医院编号获取预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result findHospDetail(@PathVariable String hoscode){
        Map<String,Object> map = hospitalServicel.findHospDetail(hoscode);
        return Result.ok(map);
    }

    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode) {
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "排班日期", required = true)
            @PathVariable String workDate) {
        return Result.ok(scheduleService.getScheduleDetail(hoscode, depcode, workDate));
    }

    @ApiOperation(value = "根据排班id获取排班信息")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(@PathVariable String scheduleId){
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return Result.ok(schedule);
    }

    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }

}
