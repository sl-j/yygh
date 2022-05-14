package com.lei.yygh.hosp.controller;


import com.lei.yygh.common.result.Result;
import com.lei.yygh.hosp.service.HospitalService;
import com.lei.yygh.vo.hosp.HospitalQueryVo;
import com.lei.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
