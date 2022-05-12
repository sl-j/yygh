package com.lei.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lei.yygh.common.exception.YyghException;
import com.lei.yygh.common.helper.HttpRequestHelper;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.result.ResultCodeEnum;
import com.lei.yygh.common.utils.MD5;
import com.lei.yygh.hosp.repository.HospitalRepository;
import com.lei.yygh.hosp.service.HospitalService;
import com.lei.yygh.hosp.service.HospitalSetService;
import com.lei.yygh.model.hosp.Hospital;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Override
    public Result saveHosp(HttpServletRequest request) {
        //获取传递来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(requestMap);

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
}
