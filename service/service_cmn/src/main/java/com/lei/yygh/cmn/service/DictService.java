package com.lei.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lei.yygh.common.result.Result;
import com.lei.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);

    void exportData(HttpServletResponse response);

    Result importDict(MultipartFile file);

    String getName(String dictCode, String value);

    Result findByDictCode(String dictCode);
}
