package com.petshop.petshop_backend.entity.dto;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PetUserAddressDTO {
    private static final long serialVersionUID = 1L;

    private Long addressId;

    private String addressReceiver;

    private String addressPhone;

    private String addressProvince;

    private String addressCity;

    private String addressDistrict;

    private String addressDetail;

    private Boolean isDefault;
}
