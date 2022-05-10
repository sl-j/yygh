package com.lei.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.model.cmn.Dict;

public interface DictService extends IService<Dict> {
    Result findChildData(Long id);
}
