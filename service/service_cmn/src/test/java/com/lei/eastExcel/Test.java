package com.lei.eastExcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        String fileName = "C:\\eastExcelTest\\test.xlsx";

        List<UserData> list = new ArrayList<>();
        for(int i = 0;i < 20;i++){
            UserData userData = new UserData();
            userData.setUid(i);
            userData.setUsername("sl:" + i);
            list.add(userData);
        }
        EasyExcel.write(fileName,UserData.class).sheet("用户信息")
                 .doWrite(list);
    }
}
