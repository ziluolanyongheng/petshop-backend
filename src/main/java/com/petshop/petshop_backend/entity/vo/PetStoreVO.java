package com.petshop.petshop_backend.entity.vo;

import com.petshop.petshop_backend.entity.PetStore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


import java.io.Serializable;
import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
public class PetStoreVO extends PetStore {

    private List<String> ImageUrl;

}
