package com.petshop.petshop_backend.entity.vo;

import com.petshop.petshop_backend.entity.PetOrder;
import com.petshop.petshop_backend.entity.PetOrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PetOrderVO extends PetOrder {
    private List<PetOrderItemVO> petOrderItems = new ArrayList<>();
}
