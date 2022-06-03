package com.lei.yygh.hosp.service;

import com.lei.yygh.common.result.Result;
import com.lei.yygh.model.hosp.Schedule;
import com.lei.yygh.vo.hosp.ScheduleOrderVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ScheduleService {
    Result saveSchedule(HttpServletRequest request);

    Result findSchedule(HttpServletRequest request);

    Result removeSchedule(HttpServletRequest request);

    Result getScheduleRule(Long page, Long limit, String hoscode, String depcode);

    Result getScheduleDetail(String hoscode, String depcode, String workDate);

    Map<String,Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    Schedule getScheduleById(String scheduleId);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //更新排班数据
    void update(Schedule schedule);
}
