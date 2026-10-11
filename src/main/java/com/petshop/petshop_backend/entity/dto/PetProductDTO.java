package com.petshop.petshop_backend.entity.dto;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class PetProductDTO {
    private static final long serialVersionUID = 1L;

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

    private String productThumb;
//    /**
//     * 0=下架,1=上架
//     */
//    private Integer productStatus;
//
//
//    private Timestamp createdTime;
}
