package com.lei.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.model.user.Patient;

import java.util.List;

public interface PatientService extends IService<Patient> {
    List<Patient> findAll(Long userId);

    Patient getPatientById(Long id);
}
