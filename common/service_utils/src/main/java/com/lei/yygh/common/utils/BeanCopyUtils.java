package com.lei.yygh.common.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;


public class BeanCopyUtils {

    //单个bean拷贝
    public static <V>V copyBean(Object source,Class<V> clazz) {

        V result = null;
        try {
            //创建目标对象
            result = clazz.newInstance();
            //使用spring中的方法实现属性copy
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    //多个bean拷贝，返回list集合
    public static <O,V>List<V> copyBeanList(List<O> list, Class<V> clazz){
        return list.stream()
                .map(o -> copyBean(o,clazz))
                .collect(Collectors.toList());
    }
}
