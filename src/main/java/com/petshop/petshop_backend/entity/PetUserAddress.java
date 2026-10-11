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
 *  实体类。
 *
 * @author Xinzhe
 * @since 2025-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_user_address")
public class PetUserAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long addressId;

    private Long userId;

    private String addressReceiver;

    private String addressPhone;

    private String addressProvince;

    private String addressCity;

    private String addressDistrict;

    private String addressDetail;

    private Boolean isDefault;

    private LocalDateTime createdTime;

}
