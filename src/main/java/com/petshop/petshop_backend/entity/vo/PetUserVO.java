package com.petshop.petshop_backend.entity.vo;


import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

@Data
public class PetUserVO {
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
     * 登录token
     */
    private String token;

    /**
     * 用户角色：0=普通用户,1=商家,2=管理员
     */
    private Integer userRole;

    /**
     * 用户类型：0=游客,1=会员
     */
    private Integer userType;

}
