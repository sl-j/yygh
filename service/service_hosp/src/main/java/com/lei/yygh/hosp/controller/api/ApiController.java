package com.lei.yygh.hosp.controller.api;

import com.lei.yygh.common.helper.HttpRequestHelper;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.hosp.service.DepartmentService;
import com.lei.yygh.hosp.service.HospitalService;
import com.lei.yygh.hosp.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "接受医院上传信息")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    //上传医院接口
    @ApiOperation(value = "上传医院接口")
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){

        return  hospitalService.saveHosp(request);
    }

    //查询医院
    @ApiOperation(value = "查询医院接口")
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        return hospitalService.getHospital(request);
    }

    //上传科室接口
    @ApiOperation(value = "上传科室接口")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        return departmentService.saveDepartment(request);
    }

    //查询科室接口
    @ApiOperation(value = "查询医院接口")
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        return departmentService.findDepartment(request);
    }

    //删除科室接口
    @ApiOperation(value = "删除医院接口")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        return departmentService.removeDepartment(request);
    }

    //上传排班接口
    @ApiOperation(value = "上传排班接口")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        return scheduleService.saveSchedule(request);
    }

    //查询排班接口
    @ApiOperation(value = "查询排班接口")
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        return scheduleService.findSchedule(request);
    }

    //删除排班接口
    @ApiOperation(value = "删除排班接口")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        return scheduleService.removeSchedule(request);
    }
}
