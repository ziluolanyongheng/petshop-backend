package com.petshop.petshop_backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

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
@Table(value = "pet_store")
public class PetStore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long storeId;

    private Long ownerId;

    private String storeName;

    private String storeDesc;

    private BigDecimal storeLng;

    private BigDecimal storeLat;

    private String storeAddress;

    private String storePhone;

    private Timestamp createdTime;

}
