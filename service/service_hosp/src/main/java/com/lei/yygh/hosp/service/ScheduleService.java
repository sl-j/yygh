package com.lei.yygh.hosp.service;

import com.lei.yygh.common.result.Result;

import javax.servlet.http.HttpServletRequest;

public interface ScheduleService {
    Result saveSchedule(HttpServletRequest request);

    Result findSchedule(HttpServletRequest request);

    Result removeSchedule(HttpServletRequest request);
}
