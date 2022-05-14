package com.lei.yygh.cmn.service.serviceImpl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lei.yygh.cmn.listener.DictListener;
import com.lei.yygh.cmn.mapper.DictMapper;
import com.lei.yygh.cmn.service.DictService;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.common.utils.BeanCopyUtils;
import com.lei.yygh.model.cmn.Dict;
import com.lei.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    @Override
    public List<Dict> findChildData(Long id) {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Dict::getParentId, id);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        //设置每个dict对象中是否有子节点
        for(Dict dict : dictList){
            Long dictId = dict.getId();
            dict.setHasChildren(isChildren(dictId));
        }
        return dictList;
    }

    @Override
    public void exportData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);
        List<DictEeVo> dictEeVos = BeanCopyUtils.copyBeanList(dictList, DictEeVo.class);

        //调用方法进行写操作
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                    .doWrite(dictEeVos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result importDict(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @Override
    public String getName(String dictCode, String value) {
        //如果dictCode为空
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isEmpty(dictCode)){
            queryWrapper.eq(Dict::getValue,value);
            Dict dict = baseMapper.selectOne(queryWrapper);
            return dict.getName();
        }else{
            //根据dictCode得到id值，再根据id值和value进行查询
            queryWrapper.eq(Dict::getDictCode,dictCode);
            Dict dict = baseMapper.selectOne(queryWrapper);
            Long id = dict.getId();
            //根据parentId和value进行查询
            return baseMapper.selectOne(new LambdaQueryWrapper<Dict>()
                    .eq(Dict::getParentId, id)
                    .eq(Dict::getValue, value)).getName();
        }
    }

    //判断id下面是否有子节点
    private boolean isChildren(Long id){
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Dict::getParentId, id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }

}
