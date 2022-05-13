package com.lei.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.model.hosp.Hospital;
import com.lei.yygh.model.hosp.HospitalSet;
import com.lei.yygh.vo.hosp.HospitalSetQueryVo;

import java.util.List;

public interface HospitalSetService extends IService<HospitalSet> {
    Result findPageHospSet(long current, long limit, HospitalSetQueryVo hospitalSetQueryVo);

    Result saveHospitalSet(HospitalSet hospitalSet);

    Result getHospSet(long id);

    Result updateHospSet(HospitalSet hospitalSet);

    Result batchRemoveHospSet(List<Long> idList);

    Result lockHospitalSet(Long id, Integer status);

    Result sendKey(Long id);

    String getSignKey(String hoscode1);
}
