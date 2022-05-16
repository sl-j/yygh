package com.lei.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lei.yygh.common.exception.YyghException;
import com.lei.yygh.common.helper.HttpRequestHelper;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.result.ResultCodeEnum;
import com.lei.yygh.common.utils.MD5;
import com.lei.yygh.hosp.repository.ScheduleRepository;
import com.lei.yygh.hosp.service.HospitalService;
import com.lei.yygh.hosp.service.HospitalSetService;
import com.lei.yygh.hosp.service.ScheduleService;
import com.lei.yygh.model.hosp.Department;
import com.lei.yygh.model.hosp.Schedule;
import com.lei.yygh.vo.hosp.BookingScheduleRuleVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

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

    //根据医院编号和科室编号，查询排班规则
    @Override
    public Result getScheduleRule(Long page, Long limit, String hoscode, String depcode) {
        //根据医院编号和科室编号查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        //select count（票） from 表 where hoscode=?and decode=? group by 日期 limit(?,?) desc;
        //2 根据工作日workDate期进行分组
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        //3 统计号源数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //排序
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                //4 实现分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );

        AggregationResults<BookingScheduleRuleVo> aggResults = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        //分组查询总的记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
        );

        AggregationResults<BookingScheduleRuleVo> totalResults = mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalResults.getMappedResults().size();

        //把日期对应星期
        for(BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList){
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //设置为map返回
        Map<String,Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
        result.put("total",total);

        //获取医院名称
        String hosName = hospitalService.getHospname(hoscode);
        Map<String,String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);

        return Result.ok(result);
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

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

}
