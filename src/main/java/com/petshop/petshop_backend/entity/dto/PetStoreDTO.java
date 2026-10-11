package com.petshop.petshop_backend.entity.dto;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class PetStoreDTO {
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long storeId;

//    private Long ownerId;

    private String storeName;

    private String storeDesc;

    private BigDecimal storeLng;

    private BigDecimal storeLat;

    private String storeAddress;

    private String storePhone;

//    private Timestamp createdTime;
}
