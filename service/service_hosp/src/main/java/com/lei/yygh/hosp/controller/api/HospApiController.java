package com.lei.yygh.hosp.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.hosp.service.DepartmentService;
import com.lei.yygh.hosp.service.HospitalService;
import com.lei.yygh.model.hosp.Hospital;
import com.lei.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

}
