package com.lei.yygh.hosp.service;

import com.lei.yygh.common.result.Result;

import javax.servlet.http.HttpServletRequest;

public interface DepartmentService {
    Result findDepartment(HttpServletRequest request);

    Result saveDepartment(HttpServletRequest request);

    Result removeDepartment(HttpServletRequest request);

    Result getDeptList(String hoscode);

    String getDepName(String hoscode, String depcode);
}
