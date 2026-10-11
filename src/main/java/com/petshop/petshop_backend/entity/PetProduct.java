package com.petshop.petshop_backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author Xinzhe
 * @since 2025-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_product")
public class PetProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long productId;

    private Long storeId;

    private String productName;

    private String productDesc;

    private String productSpec;
    /**
     * 1=宠物,2=周边
     */
    private Integer productType;

    private BigDecimal productPrice;

    private Integer productStock;

    /**
     * 0=下架,1=上架
     */
    private Integer productStatus;

    private String productThumb;

    private LocalDateTime createdTime;

}
