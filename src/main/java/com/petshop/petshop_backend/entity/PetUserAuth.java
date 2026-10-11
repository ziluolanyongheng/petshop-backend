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
 * 用户认证表 实体类。
 *
 * @author Xinzhe
 * @since 2025-06-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_user_auth")
public class PetUserAuth implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 认证ID（主键）
     */
    @Id(keyType = KeyType.Auto)
    private Long authId;

    /**
     * 用户ID（外键）
     */
    private Long userId;

    /**
     * 认证类型：password/weixin/alipay/fingerprint/face/email/qq
     */
    private String authType;

    /**
     * 第三方ID/生物特征ID
     */
    private String authKey;

    /**
     * 访问令牌(验证码Code)
     */
    private String authToken;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    private String userEmail;

}
