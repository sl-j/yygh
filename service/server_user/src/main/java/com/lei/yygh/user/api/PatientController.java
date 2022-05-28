package com.lei.yygh.user.api;

import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.utils.AuthContextHolder;
import com.lei.yygh.model.user.Patient;
import com.lei.yygh.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "就诊人管理")
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;
    //获取就诊人列表
    @ApiOperation(value = "获取就诊人列表")
    @GetMapping("auth/findAll")
    public Result findAll(HttpServletRequest request){
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        //根据id查询就诊人信息(一个用户可能有多个就诊人)
        List<Patient> list = patientService.findAll(userId);
        return Result.ok(list);
    }

    //添加就诊人
    @ApiOperation(value = "添加就诊人")
    @PostMapping("auth/save")
    public Result save(@RequestBody Patient patient,HttpServletRequest request){
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    //根据id获取就诊人信息
    @ApiOperation(value = "根据id获取就诊人信息")
    @GetMapping("auth/get/{id}")
    public Result getPatientById(@PathVariable Long id){
        Patient patient = patientService.getPatientById(id);
        return Result.ok(patient);
    }

    //修改就诊人信息
    @ApiOperation(value = "修改就诊人")
    @PostMapping("auth/update")
    public Result update(@RequestBody Patient patient){
        patientService.updateById(patient);
        return Result.ok();
    }

    //删除就诊人信息
    @ApiOperation(value = "删除就诊人")
    @DeleteMapping("auth/delete/{id}")
    public Result delete(@RequestBody Long id){
        patientService.removeById(id);
        return Result.ok();
    }

}
