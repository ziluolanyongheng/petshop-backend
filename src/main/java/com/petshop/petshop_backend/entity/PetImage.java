package com.petshop.petshop_backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片资源表 实体类。
 *
 * @author user
 * @since 2025-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_image")
public class PetImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片ID（主键）
     */
    @Id(keyType = KeyType.Auto)
    private Long imgId;

    /**
     * 图片URL
     */
    private String imgUrl;

    /**
     * 图片类型：avatar/product/store/video
     */
    private String imgType;

    /**
     * 上传时间
     */
    private Timestamp createdTime;

}
