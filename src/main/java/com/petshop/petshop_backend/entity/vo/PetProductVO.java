package com.petshop.petshop_backend.entity.vo;

import com.petshop.petshop_backend.entity.PetProduct;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PetProductVO extends PetProduct {
    private List<String> productUrls;
}
