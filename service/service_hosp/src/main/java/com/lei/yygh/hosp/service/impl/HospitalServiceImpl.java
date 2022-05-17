package com.lei.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lei.yygh.cmn.client.DictFeignClient;
import com.lei.yygh.common.exception.YyghException;
import com.lei.yygh.common.helper.HttpRequestHelper;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.result.ResultCodeEnum;
import com.lei.yygh.common.utils.BeanCopyUtils;
import com.lei.yygh.common.utils.MD5;
import com.lei.yygh.hosp.repository.DepartmentRepository;
import com.lei.yygh.hosp.repository.HospitalRepository;
import com.lei.yygh.hosp.repository.ScheduleRepository;
import com.lei.yygh.hosp.service.HospitalService;
import com.lei.yygh.hosp.service.HospitalSetService;

import com.lei.yygh.model.hosp.Department;
import com.lei.yygh.model.hosp.Hospital;
import com.lei.yygh.model.hosp.Schedule;
import com.lei.yygh.vo.hosp.DepartmentQueryVo;
import com.lei.yygh.vo.hosp.HospitalQueryVo;
import com.lei.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.redis.connection.ReactiveSetCommands;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DictFeignClient dictFeignClient;


    @Override
    public Result saveHosp(HttpServletRequest request) {
        //获取传递来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(requestMap);

        //验证签名信息
        if(!validSignKey(map)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //获取logoData，进行转换
        String logoData = (String) map.get("logoData");
        logoData = logoData.replace(" ","+");
        map.put("logoData",logoData);


        //map转换成对象
        String mapString = JSONObject.toJSONString(map);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //判断是否存在相同数据
        String hoscode = hospital.getHoscode();
        Hospital hospital1Exist = hospitalRepository.getHospitalByHoscode(hoscode);

        if(hospital1Exist != null){
            hospital.setStatus(hospital1Exist.getStatus());
            hospital.setCreateTime(hospital1Exist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }

        return Result.ok();
    }

    @Override
    public Result getHospital(HttpServletRequest request) {
        //获取传递来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号
        String hoscode = (String) map.get("hoscode");

        //验证签名信息
        if(!validSignKey(map)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //根据医院编号查询医院信息
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);;

        return Result.ok(hospital);
    }

    @Override
    public Result selecthospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageAble对象
        Pageable pageable = PageRequest.of(page - 1,limit);
        //创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        //vo转化为hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);

        Example<Hospital> example = Example.of(hospital,matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);


        //获取list集合，通过stream流的方式进行遍历，将医院等级进行映射，这是一个mongo跨mysql的过程
        pages.getContent().stream().forEach(item ->{
            this.setHospitalHosType(item);
        });

        return Result.ok(pages);
    }

    //更新医院上线状态
    @Override
    public Result updateHospStatus(String id, Integer status) {
        //根据医院id查询医院信息
        Hospital hospital = hospitalRepository.findById(id).get();
        //修改状态
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
        return Result.ok();
    }

    //医院详情信息
    @Override
    public Result showHospDetail(String id) {
        Map<String,Object> result = new HashMap<>();
        //将查询出的医院信息进行映射
        Hospital hospital = setHospitalHosType(hospitalRepository.findById(id).get());

        result.put("hospital",hospital);
        result.put("bookingRule",hospital.getBookingRule());

        //已经单独将预约规则提出，所以将原本的设置为null
        hospital.setBookingRule(null);

        return Result.ok(result);
    }

    //获取医院名称
    @Override
    public String getHospname(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode).getHosname();
    }


    //将遍历的每一个医院的医院等级进行映射  dict_code是mysql的数据字典表中的dict_code,value是mongo中的hostype
    private Hospital setHospitalHosType(Hospital hospital) {
        //根据distCode和value获取医院等级的名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());

        //查询省 市和区的信息
        String provinceName = dictFeignClient.getName(hospital.getProvinceCode());
        String cityName = dictFeignClient.getName(hospital.getCityCode());
        String districtName = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString",hostypeString);
        hospital.getParam().put("fullAddress",provinceName + cityName + districtName);

        return hospital;
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
