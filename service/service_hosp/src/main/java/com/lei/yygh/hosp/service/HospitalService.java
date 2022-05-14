package com.lei.yygh.hosp.service;

import com.lei.yygh.common.result.Result;
import com.lei.yygh.vo.hosp.HospitalQueryVo;
import com.lei.yygh.vo.hosp.HospitalSetQueryVo;

import javax.servlet.http.HttpServletRequest;

public interface HospitalService {
    Result saveHosp(HttpServletRequest request);

    Result getHospital(HttpServletRequest request);

    Result selecthospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);
}
