package com.lei.yygh.hosp.service;

import com.lei.yygh.common.result.Result;

import javax.servlet.http.HttpServletRequest;

public interface HospitalService {
    Result saveHosp(HttpServletRequest request);
}
