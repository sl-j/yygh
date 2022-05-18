package com.lei.yygh.hosp.controller;

import com.lei.yygh.common.result.Result;
import com.lei.yygh.hosp.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "科室信息接口")
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    //根据医院编号，查询所有科室
    @ApiOperation(value = "根据医院编号，查询所有科室")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode){
        return departmentService.getDeptList(hoscode);
    }

}
