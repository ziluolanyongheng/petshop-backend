package com.petshop.petshop_backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表 实体类。
 *
 * @author Xinzhe
 * @since 2025-06-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_user")
public class PetUser implements Serializable {

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

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
