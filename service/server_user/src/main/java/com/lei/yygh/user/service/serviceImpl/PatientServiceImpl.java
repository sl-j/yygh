package com.lei.yygh.user.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lei.yygh.cmn.client.DictFeignClient;
import com.lei.yygh.enums.DictEnum;
import com.lei.yygh.model.user.Patient;
import com.lei.yygh.model.user.UserInfo;
import com.lei.yygh.user.mapper.PatientMapper;
import com.lei.yygh.user.mapper.UserInfoMapper;
import com.lei.yygh.user.service.PatientService;
import com.lei.yygh.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    //获取就诊人列表
    @Override
    public List<Patient> findAll(Long userId) {
        //根据id查询就诊人所有信息
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getUserId,userId);
        List<Patient> patientList = baseMapper.selectList(queryWrapper);
        //通过远程调用得到编码对应的内容，查询数据字典的内容
        patientList.stream().forEach(item ->{
            //其他内容的封装
            this.packPatient(item);
        });
        return patientList;
    }

    @Override
    public Patient getPatientById(Long id) {
        Patient patient = baseMapper.selectById(id);
        return packPatient(patient);
    }

    //patient对象其他参数的封装
    private Patient packPatient(Patient patient) {
        //根据证件类型编码，获取证件类型
        String certificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());//联系人证件
        //联系人证件类型
        String contactsCertificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        //市
        String cityString = dictFeignClient.getName(patient.getCityCode());
        //区
        String districtString = dictFeignClient.getName(patient.getDistrictCode());

        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }
}
