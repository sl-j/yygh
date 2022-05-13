package com.lei.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lei.yygh.common.exception.YyghException;
import com.lei.yygh.common.helper.HttpRequestHelper;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.result.ResultCodeEnum;
import com.lei.yygh.common.utils.MD5;
import com.lei.yygh.hosp.repository.DepartmentRepository;
import com.lei.yygh.hosp.service.DepartmentService;
import com.lei.yygh.hosp.service.HospitalSetService;
import com.lei.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Override
    public Result saveDepartment(HttpServletRequest request) {
        //获取传递来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(requestMap);

        //验证签名信息
        if(!validSignKey(map)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //map转换成对象
        String mapString = JSONObject.toJSONString(map);
        Department department = JSONObject.parseObject(mapString, Department.class);

        //根据医院编号和科室编号进行查询
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        if(departmentExist != null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else{
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }

        return Result.ok();
    }

    @Override
    public Result findDepartment(HttpServletRequest request) {
        //获取传递来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(requestMap);

        //验证签名信息
        if(!validSignKey(map)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        String hoscode = (String) map.get("hoscode");

        //当前页和记录数
        Integer page = StringUtils.isEmpty(map.get("page")) ? 1 : Integer.parseInt((String)map.get("page"));
        Integer limit = StringUtils.isEmpty(map.get("limit")) ? 1 : Integer.parseInt((String)map.get("limit"));

        Department department = new Department();
        department.setHoscode(hoscode);
        department.setIsDeleted(0);

        //0是第一页
        Pageable pageable = PageRequest.of(page - 1,limit);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department,matcher);
        Page<Department> departments = departmentRepository.findAll(example, pageable);
        return Result.ok(departments);
    }

    @Override
    public Result removeDepartment(HttpServletRequest request) {
        //获取传递来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(requestMap);

        //验证签名信息
        if(!validSignKey(map)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        String hoscode = (String) map.get("hoscode");
        String depcode = (String) map.get("depcode");

        //根据医院编号和科室编号进行查询科室是否存在
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode,depcode);

        if(departmentExist != null){
            departmentRepository.deleteById(departmentExist.getId());
        }else{
            return Result.fail(ResultCodeEnum.DATA_ERROR);
        }

        return Result.ok();
    }

    //验证签名信息
    public boolean validSignKey(Map<String, Object> map){
        //获取医院系统传递来的签名
        String hospSign = (String) map.get("sign");
        //获取医院的编号
        String hoscode1 = (String) map.get("hoscode");
        //根据编号查询数据库中的签名
        String signKey = hospitalSetService.getSignKey(hoscode1);
        //查询出的签名进行MD5加密
        String encryptKeyMD5 = MD5.encrypt(signKey);
        //判断和医院传来的签名是否一致
        if(!encryptKeyMD5.equals(hospSign)){
            return false;
        }
        return true;
    }
}
