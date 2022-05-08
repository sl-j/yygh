package com.lei.yygh.hosp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.hosp.service.HospitalSetService;
import com.lei.yygh.model.hosp.HospitalSet;
import com.lei.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 查询医院信息
     * @return
     */
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitalSet(){
        return Result.ok(hospitalSetService.list());
    }
    /**
     * 删除医院信息
     */
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);

        if(flag){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    //条件查询带分页
    @ApiOperation("分页查询医院设置（带模糊）")
    @PostMapping("findPage/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        return hospitalSetService.findPageHospSet(current,limit,hospitalSetQueryVo);
    }

    /**
     * 添加医院设置
     */
    @ApiOperation("添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        return hospitalSetService.saveHospitalSet(hospitalSet);
    }

    /**
     * 根据id查询医院设置
     */
    @ApiOperation("根据id查询医院设置")
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable long id){
        return hospitalSetService.getHospSet(id);
    }

    /**
     * 修改医院设置
     */
    @ApiOperation("修改医院设置")
    @PostMapping("updateHospSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet){
        return hospitalSetService.updateHospSet(hospitalSet);
    }

    /**
     * 批量删除医院设置
     */
    @ApiOperation("批量删除医院设置")
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospSet(@RequestBody List<Long> idList){
        return hospitalSetService.batchRemoveHospSet(idList);
    }
}
