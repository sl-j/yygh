package com.lei.yygh.hosp.repository;

import com.lei.yygh.model.hosp.Department;
import com.lei.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
