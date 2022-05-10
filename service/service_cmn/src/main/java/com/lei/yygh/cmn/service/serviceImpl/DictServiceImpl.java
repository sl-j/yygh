package com.lei.yygh.cmn.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lei.yygh.cmn.mapper.DictMapper;
import com.lei.yygh.cmn.service.DictService;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.model.cmn.Dict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public Result findChildData(Long id) {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Dict::getParentId, id);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        //设置每个dict对象中是否有子节点
        for(Dict dict : dictList){
            Long dictId = dict.getId();
            dict.setHasChildren(isChildren(dictId));
        }
        return Result.ok(dictList);
    }

    //判断id下面是否有子节点
    private boolean isChildren(Long id){
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Dict::getParentId, id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }

}
