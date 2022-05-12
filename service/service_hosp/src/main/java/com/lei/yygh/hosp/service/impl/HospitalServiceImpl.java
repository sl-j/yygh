package com.lei.yygh.hosp.service.impl;

import com.lei.yygh.hosp.repository.HospitalRepository;
import com.lei.yygh.hosp.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;
}
