package com.petshop.petshop_backend.entity.vo;


import com.petshop.petshop_backend.entity.PetCart;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class PetCartVO extends PetCart {
    //缩略图
    private String productThumb;

    //商品名
    private String productName;

    //商品价格
    private BigDecimal productPrice;
}
