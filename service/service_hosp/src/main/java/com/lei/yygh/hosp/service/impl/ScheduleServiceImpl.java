package com.lei.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lei.yygh.common.exception.YyghException;
import com.lei.yygh.common.helper.HttpRequestHelper;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.result.ResultCodeEnum;
import com.lei.yygh.common.utils.MD5;
import com.lei.yygh.hosp.repository.ScheduleRepository;
import com.lei.yygh.hosp.service.HospitalSetService;
import com.lei.yygh.hosp.service.ScheduleService;
import com.lei.yygh.model.hosp.Department;
import com.lei.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Override
    public Result saveSchedule(HttpServletRequest request) {
        //获取传递来的排班信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(requestMap);

        //验证签名信息
        if(!validSignKey(map)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //map转换成对象
        String mapString = JSONObject.toJSONString(map);
        Schedule schedule = JSONObject.parseObject(mapString, Schedule.class);

        //根据医院编号和排班编号进行查询
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());

        if(scheduleExist != null){
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        }else{
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }

        return Result.ok();
    }

    @Override
    public Result findSchedule(HttpServletRequest request) {
        //获取传递来的排班信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(requestMap);

        //验证签名信息
        if(!validSignKey(map)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        String hoscode = (String) map.get("hoscode");
        String depcode = (String) map.get("depcode");

        //当前页和记录数
        Integer page = StringUtils.isEmpty(map.get("page")) ? 1 : Integer.parseInt((String)map.get("page"));
        Integer limit = StringUtils.isEmpty(map.get("limit")) ? 1 : Integer.parseInt((String)map.get("limit"));

        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        schedule.setDepcode(depcode);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);

        //0是第一页
        Pageable pageable = PageRequest.of(page - 1,limit);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule,matcher);
        Page<Schedule> schedules = scheduleRepository.findAll(example, pageable);
        return Result.ok(schedules);
    }

    @Override
    public Result removeSchedule(HttpServletRequest request) {
        //获取传递来的排班信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(requestMap);

        //验证签名信息
        if(!validSignKey(map)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        String hoscode = (String) map.get("hoscode");
        String hosScheduleId = (String) map.get("hosScheduleId");

        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(schedule != null){
            scheduleRepository.deleteById(schedule.getId());
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
