package com.lei.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.lei.yygh.cmn.mapper.DictMapper;
import com.lei.yygh.common.utils.BeanCopyUtils;
import com.lei.yygh.model.cmn.Dict;
import com.lei.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.factory.annotation.Autowired;

public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public DictListener(DictMapper dictMapper){
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = BeanCopyUtils.copyBean(dictEeVo, Dict.class);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
