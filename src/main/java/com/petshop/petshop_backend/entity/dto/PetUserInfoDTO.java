package com.petshop.petshop_backend.entity.dto;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class PetUserInfoDTO {


    private static final long serialVersionUID = 1L;

    /**
     * 加密密码
     */
    private String userPassword;

    /**
     * 邮箱（唯一）
     */
    private String userEmail;

    /**
     * 手机号
     */
    private String userPhone;


    /**
     * 用户头像链接
     */
    private String PetImageUrl;

}
