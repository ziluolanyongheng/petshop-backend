package com.petshop.petshop_backend.entity.dto;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

@Data
public class PetUserDTO {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（主键）
     */
    @Id(keyType = KeyType.Auto)
    private Long userId;

    /**
     * 用户名（唯一）
     */
    private String userName;

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
     * 用户角色：0=普通用户,1=商家,2=管理员
     */
    private Integer userRole;

    /**
     * 用户类型：0=游客,1=会员
     */
    private Integer userType;

}
