package com.lei.yygh.hosp.controller;


import com.lei.yygh.common.result.Result;
import com.lei.yygh.hosp.service.HospitalService;
import com.lei.yygh.vo.hosp.HospitalQueryVo;
import com.lei.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "医院信息接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    //医院列表(条件查询带分页
    @ApiOperation(value = "医院列表(条件查询带分页）")
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        return hospitalService.selecthospPage(page,limit,hospitalQueryVo);
    }

    //更新医院上线状态
    @ApiOperation(value = "更新医院上线状态")
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,
                                   @PathVariable Integer status){
        return hospitalService.updateHospStatus(id,status);
    }

    //医院详情信息
    @ApiOperation(value = "医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        return hospitalService.showHospDetail(id);
    }
}
