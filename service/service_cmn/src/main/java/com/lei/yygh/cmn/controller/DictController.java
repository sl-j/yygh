package com.lei.yygh.cmn.controller;

import com.lei.yygh.cmn.service.DictService;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {
    @Autowired
    private DictService dictService;

    //根据id查询子数据

    @ApiOperation(value = "根据数据字典id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> dictList = dictService.findChildData(id);
        return Result.ok(dictList);
    }

    //导出数据字典接口
    @ApiOperation(value = "导出数据字典接口")
    @GetMapping("exportData")
    public void exportData(HttpServletResponse response){
        dictService.exportData(response);
    }

    //导入数据字典
    @CacheEvict(value = "dict",allEntries = true)
    @ApiOperation(value = "导入数据字典接口")
    @PostMapping("importData")
    public Result importDict(MultipartFile file){
        return dictService.importDict(file);
    }


    //根据dictcode和value查询
    @ApiOperation(value = "根据dictcode和value查询数据字典名称")
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        return dictService.getName(dictCode,value);
    }

    //根据value查询数据字典的名称
    @ApiOperation(value = "根据value查询数据字典的名称")
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        return dictService.getName("",value);
    }


}
