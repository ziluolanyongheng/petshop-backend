package com.petshop.petshop_backend.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PetCartWithStoreInfoVO {
    private Long storeId;
    private String storeName;
    private String storeImg;
    private List<PetCartVO> petCartVOList = new ArrayList<>();
}
