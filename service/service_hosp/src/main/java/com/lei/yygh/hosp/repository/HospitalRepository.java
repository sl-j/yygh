package com.lei.yygh.hosp.repository;

import com.lei.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    //判断mongoDB中是否存在数据
    Hospital getHospitalByHoscode(String hoscode);
}
