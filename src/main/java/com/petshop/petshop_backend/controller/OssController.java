package com.petshop.petshop_backend.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.entity.PetImage;
import com.petshop.petshop_backend.entity.PetImageRel;
import com.petshop.petshop_backend.service.PetImageRelService;
import com.petshop.petshop_backend.service.PetImageService;
import com.petshop.petshop_backend.utils.MinIOUtil;
import com.petshop.petshop_backend.utils.OssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * OSS 文件控制器
 */
@Tag(name = "4 文件上传接口")
@RestController
@RequestMapping("/oss")
public class OssController {
    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private PetImageService petImageService;

    @Autowired
    private PetImageRelService petImageRelService;

    @Operation(summary = "上传图片并保存记录")
    @ApiOperationSupport(order = 10)
    @PostMapping("/uploadImage")
    public Result uploadImage(
            MultipartFile multipartFile,
            @RequestParam("imgType") String imgType,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") Long entityId,
            @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary,
            @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder) {

        // 1. 上传文件到OSS
        String imgUrl = ossUtil.uploadFile(multipartFile, imgType,entityId);
        if (imgUrl == null) {
            return Result.error("文件上传失败");
        }


        // 2. 保存图片记录
        PetImage petImage = new PetImage();
        petImage.setImgUrl(imgUrl);
        petImage.setImgType(imgType);
        petImage.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));

        petImageService.save(petImage);

        // 3. 保存图片关联记录
        PetImageRel petImageRel = new PetImageRel();
        petImageRel.setImgId(petImage.getImgId());
        petImageRel.setEntityType(entityType);
        petImageRel.setEntityId(entityId);
        petImageRel.setIsPrimary(isPrimary);
        petImageRel.setSortOrder(sortOrder);
        petImageRel.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));

        if (!Objects.equals(entityType, "product")) {
            List<PetImageRel> petImageRels= petImageRelService.getByEntityTypeAndEntityId(petImageRel.getEntityType(),petImageRel.getEntityId());
            for (PetImageRel petImageRel1 : petImageRels) {
                petImageRelService.removeById(petImageRel1.getImgId());
            }
        }

        petImageRelService.save(petImageRel);

        return Result.success(petImage);
    }

    @Operation(summary = "上传网络图片并保存记录")
    @ApiOperationSupport(order = 11)
    @PostMapping("/uploadOtherImage")
    public Result uploadOtherImage(
            @RequestParam("imgUrl") String imgUrl,
            @RequestParam("imgType") String imgType,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") Long entityId,
            @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary,
            @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder) {


        // 2. 保存图片记录
        PetImage petImage = new PetImage();
        petImage.setImgUrl(imgUrl);
        petImage.setImgType(imgType);
        petImage.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));


        // 3. 保存图片关联记录
        PetImageRel petImageRel = new PetImageRel();
        petImageRel.setImgId(petImage.getImgId());
        petImageRel.setEntityType(entityType);
        petImageRel.setEntityId(entityId);
        petImageRel.setIsPrimary(isPrimary);
        petImageRel.setSortOrder(sortOrder);
        petImageRel.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
        if (!Objects.equals(entityType, "product")) {
            List<PetImageRel> petImageRels= petImageRelService.getByEntityTypeAndEntityId(petImageRel.getEntityType(),petImageRel.getEntityId());
            for (PetImageRel petImageRel1 : petImageRels) {
                petImageService.removeById(petImageRel1.getImgId());
            }
        }
        petImageService.save(petImage);
        petImageRelService.save(petImageRel);
        return Result.success(petImage);
    }

    @Operation(summary = "获取图片URL")
    @ApiOperationSupport(order = 11)
    @GetMapping("/getImageUrl")
    public List<String> getImageUrl(
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") Long entityId) {
        // 1. 通过实体类型和ID获取图片关联记录
        List<PetImageRel> imageRels=petImageRelService.getByEntityTypeAndEntityId(entityType, entityId);
        if (imageRels.isEmpty()) {
            if(!Objects.equals(entityType, "product")){
                PetImageRel petImageRel=new PetImageRel();
                petImageRel.setImgId(48L);
                petImageRel.setEntityId(entityId);
                petImageRel.setEntityType(entityType);
                petImageRel.setIsPrimary(true);
                petImageRel.setSortOrder(1);
                petImageRel.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                petImageRelService.save(petImageRel);
                imageRels= Collections.singletonList(petImageRel);
            }else {
            return Collections.singletonList("未找到相关图片");
            }
        }

        // 2. 通过img_id获取图片记录
        List<PetImage> petImages=new ArrayList<>();
        for (PetImageRel petImageRel : imageRels) {
            petImages.add(petImageService.getById(petImageRel.getImgId()));
        }
        List<String> petImageUrls=new ArrayList<>();
        for (PetImage petImage : petImages) {
            petImageUrls.add(petImage.getImgUrl());
        }
        return petImageUrls;
    }
    @Operation(summary = "MinIO图片上传")
    @ApiOperationSupport(order = 20)
    @PostMapping("/minioUpload")
    public Result uploadToMinio(MultipartFile filedata) throws IOException {

        String upload = MinIOUtil.upload(filedata);

        if(upload==null){
            return Result.error("上传文件失败");
        }else{
            return Result.success(upload);
        }
    }
}