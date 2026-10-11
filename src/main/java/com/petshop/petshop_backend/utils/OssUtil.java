package com.petshop.petshop_backend.utils;

import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.petshop.petshop_backend.config.OssConfig;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
 
import java.io.*;
import java.time.LocalDateTime;
import java.util.UUID;
 
/**
 * OSS 文件管理服务
 */
@Log4j2
@Component
public class OssUtil {
 
    /** 自动注入 OssConfig 类型的 Bean */
    @Autowired
    private OssConfig ossConfig;
 
    /** 定义访问前缀，用于构建文件的完整访问路径 */
    @Value("${aliyun.oss.accessPre}")
    private String accessPre;
 
    /** 定义存储桶名称，方便在上传和下载时引用 */
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
 
    /**
     * 根据图片类型获取存储路径
     *
     * @param imgType 图片类型
     * @return 存储路径
     */
    private String getStoragePath(String imgType) {
        switch (imgType.toLowerCase()) {
            case "product":
                return "productImages/";
            case "store":
                return "storeImages/";
            case "avatar":
                return "userImages/";
            default:
                throw new IllegalArgumentException("不支持的图片类型: " + imgType);
        }
    }
 
    /**
     * 上传文件到OSS
     *
     * @param multipartFile 文件
     * @param imgType 图片类型
     * @param entityId 实体ID
     * @return 文件访问路径
     */
    public String uploadFile(MultipartFile multipartFile, String imgType, Long entityId) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
            String ossPath = getStoragePath(imgType) + imgType + "_" + entityId + "_"+LocalDateTime.now()+ "."  + fileSuffix;
            uploadFileInputStreamForBucket(bucketName, ossPath, inputStream);
            return accessPre + ossPath;
        } catch (IOException e) {
            log.error("上传文件失败: {}", e.getMessage(), e);
            return null;
        }
    }
 
    /**
     * 上传文件到OSS（自定义文件名）
     *
     * @param multipartFile 文件
     * @param imgType 图片类型
     * @param entityId 实体ID
     * @return 文件访问路径
     */
    public String uploadFileWithCustomName(MultipartFile multipartFile, String imgType, Long entityId) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
            String ossPath = getStoragePath(imgType) + imgType + "_" + entityId +"_" + LocalDateTime.now() + "." + fileSuffix;
            uploadFileInputStreamForBucket(bucketName, ossPath, inputStream);
            return accessPre + ossPath;
        } catch (IOException e) {
            log.error("上传文件失败: {}", e.getMessage(), e);
            return null;
        }
    }
 
    /**
     * 使用文件流上传到指定的 Bucket 实例
     */
    private void uploadFileInputStreamForBucket(String bucketName, String ossPath, InputStream inputStream) {
        ossConfig.init().putObject(bucketName, ossPath, inputStream);
    }
 
    /**
     * 获取文件URL
     *
     * @param imgType 图片类型
     * @param fileName 文件名
     * @return 文件URL
     */
    public String getFileUrl(String imgType, String fileName) {
        String ossPath = getStoragePath(imgType) + fileName;
        return accessPre + ossPath;
    }
 
    /**
     * 检查文件是否存在
     *
     * @param imgType 图片类型
     * @param fileName 文件名
     * @return 是否存在
     */
    public boolean doesFileExist(String imgType, String fileName) {
        String ossPath = getStoragePath(imgType) + fileName;
        return ossConfig.init().doesObjectExist(bucketName, ossPath);
    }

    /**
     * 根据实体类型和实体ID获取图片URL
     *
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @return 图片URL
     */
    public String getFileUrlByEntity(String entityType, Long entityId) {
        // 从URL中提取文件名
        String fileName = entityType + "_" + entityId;
        String imgType = getImgTypeByEntityType(entityType);
        return getFileUrl(imgType, fileName);
    }

    /**
     * 根据实体类型获取对应的图片类型
     *
     * @param entityType 实体类型
     * @return 图片类型
     */
    private String getImgTypeByEntityType(String entityType) {
        switch (entityType.toLowerCase()) {
            case "product":
                return "product";
            case "store":
                return "store";
            case "avatar":
                return "avatar";
            default:
                throw new IllegalArgumentException("不支持的实体类型: " + entityType);
        }
    }
}