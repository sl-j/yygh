package com.lei.yygh.hosp.service;

import com.lei.yygh.common.result.Result;
import com.lei.yygh.model.hosp.Hospital;
import com.lei.yygh.vo.hosp.HospitalQueryVo;
import com.lei.yygh.vo.hosp.HospitalSetQueryVo;
import com.lei.yygh.vo.order.SignInfoVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface HospitalService {
    Result saveHosp(HttpServletRequest request);

    Result getHospital(HttpServletRequest request);

    Result selecthospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    Result updateHospStatus(String id, Integer status);

    Result showHospDetail(String id);

    String getHospname(String hoscode);

    //根据医院名称查询
    List<Hospital> findByHosName(String hosname);


    Map<String, Object> findHospDetail(String hoscode);

    Hospital getByHoscode(String hoscode);
}
