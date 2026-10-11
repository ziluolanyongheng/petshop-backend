package com.petshop.petshop_backend.entity.vo;

import com.petshop.petshop_backend.entity.PetOrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PetOrderItemVO extends PetOrderItem {
    private String StoreImg;
    private String StoreName;
    private String productName;
    private String productThumb;
}
