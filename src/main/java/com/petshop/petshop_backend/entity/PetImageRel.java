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
 * 图片关联表 实体类。
 *
 * @author user
 * @since 2025-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_image_rel")
public class PetImageRel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID（主键）
     */
    @Id(keyType = KeyType.Auto)
    private Long imgRelId;

    /**
     * 图片ID（外键）
     */
    private Long imgId;

    /**
     * 关联实体类型：user/product/store/video
     */
    private String entityType;

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 是否主图
     */
    private Boolean isPrimary;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private Timestamp createdTime;

}
