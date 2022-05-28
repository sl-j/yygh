package com.lei.yygh.hosp.service;

import com.lei.yygh.common.result.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ScheduleService {
    Result saveSchedule(HttpServletRequest request);

    Result findSchedule(HttpServletRequest request);

    Result removeSchedule(HttpServletRequest request);

    Result getScheduleRule(Long page, Long limit, String hoscode, String depcode);

    Result getScheduleDetail(String hoscode, String depcode, String workDate);

    Map<String,Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);
}
