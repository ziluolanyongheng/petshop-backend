package com.petshop.petshop_backend.entity.dto;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PetCartDTO {
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long cartId;

    private Long userId;

    private Long productId;

    private Integer cartQuantity;

    private String selectedSpec;

    private Boolean isSelected;
}
