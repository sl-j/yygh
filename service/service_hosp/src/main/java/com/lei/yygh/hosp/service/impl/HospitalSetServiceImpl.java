package com.lei.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.result.ResultCodeEnum;
import com.lei.yygh.common.utils.MD5;
import com.lei.yygh.hosp.mapper.HospitalSetMapper;
import com.lei.yygh.hosp.service.HospitalSetService;
import com.lei.yygh.model.hosp.HospitalSet;
import com.lei.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Random;

@Service("HospitalSetService")
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Autowired
    private HospitalSetMapper hospitalSetMapper;

    @Override
    public Result findPageHospSet(long current, long limit, HospitalSetQueryVo hospitalSetQueryVo) {
        //创建page对象
        Page<HospitalSet> page = new Page<>(current,limit);
        //分页查询
        LambdaQueryWrapper<HospitalSet> queryWrapper = new LambdaQueryWrapper<>();
        //根据医院名称和医院编号模糊查询
        String hosname = null;
        String hocode = null;
        if(hospitalSetQueryVo != null){
            hosname = hospitalSetQueryVo.getHosname();
            hocode = hospitalSetQueryVo.getHoscode();
        }

        if(!StringUtils.isEmpty(hosname)){
            queryWrapper.like(HospitalSet::getHosname,hospitalSetQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hocode)){
            queryWrapper.like(HospitalSet::getHoscode,hospitalSetQueryVo.getHoscode());
        }

        Page<HospitalSet> hospitalSetPage = hospitalSetMapper.selectPage(page, queryWrapper);
        return Result.ok(hospitalSetPage);
    }

    @Override
    public Result saveHospitalSet(HospitalSet hospitalSet) {
        //设置状态
        hospitalSet.setStatus(1);
        //签名密钥
        Random random = new Random();
        String encrypt = MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000));
        hospitalSet.setSignKey(encrypt);

        int flag = hospitalSetMapper.insert(hospitalSet);
        if(flag == 0) return Result.fail(ResultCodeEnum.FAIL);
        return Result.ok();
    }

    @Override
    public Result getHospSet(long id) {
        HospitalSet hospitalSet = hospitalSetMapper.selectById(id);

        return Result.ok(hospitalSet);
    }

    @Override
    public Result updateHospSet(HospitalSet hospitalSet) {
        int flag = hospitalSetMapper.updateById(hospitalSet);
        if(flag == 0){
            return Result.fail();
        }
        return Result.ok();
    }

    @Override
    public Result batchRemoveHospSet(List<Long> idList) {
        hospitalSetMapper.deleteBatchIds(idList);
        return Result.ok();
    }

    @Override
    public Result lockHospitalSet(Long id, Integer status) {
        //根据id查询医院信息
        HospitalSet hospitalSet = hospitalSetMapper.selectById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //修改状态
        hospitalSetMapper.updateById(hospitalSet);
        return Result.ok();
    }

    @Override
    public Result sendKey(Long id) {
        HospitalSet hospitalSet = hospitalSetMapper.selectById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }

    @Override
    public String getSignKey(String hoscode1) {
        LambdaQueryWrapper<HospitalSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HospitalSet::getHoscode,hoscode1);
        HospitalSet hospitalSet = baseMapper.selectOne(queryWrapper);
        return hospitalSet.getSignKey();
    }
}
