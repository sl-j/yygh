package com.lei.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.lei.yygh.common.exception.YyghException;
import com.lei.yygh.common.result.ResultCodeEnum;
import com.lei.yygh.oss.service.FileService;
import com.lei.yygh.oss.utils.ConstantOssPropertiesUtils;
import com.lei.yygh.oss.utils.PathUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String fileUpload(MultipartFile file) {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = ConstantOssPropertiesUtils.ENDPOINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantOssPropertiesUtils.ACCESS_KEY_SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantOssPropertiesUtils.BUCKET;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();

            if(!(originalFilename.endsWith(".png") || originalFilename.endsWith(".jpg"))){
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
            String fileName = PathUtils.generateFilePath(originalFilename);
            //调用方法实现上传
            ossClient.putObject(bucketName,fileName,inputStream);

            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            //返回文件路径
            return null;
        }finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }

    }
}
