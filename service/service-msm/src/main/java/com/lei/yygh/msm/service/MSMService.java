package com.lei.yygh.msm.service;

import com.lei.yygh.vo.msm.MsmVo;

public interface MSMService {
    boolean send(String phone, String code);

    boolean send(MsmVo msmVo);
}
