package com.lei.yygh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lei.yygh.model.user.Patient;
import com.lei.yygh.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
