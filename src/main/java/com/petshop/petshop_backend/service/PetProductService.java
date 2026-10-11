package com.petshop.petshop_backend.service;

import com.mybatisflex.core.service.IService;
import com.petshop.petshop_backend.entity.PetProduct;
import com.petshop.petshop_backend.entity.vo.PetProductVO;

import java.util.List;

/**
 *  服务层。
 *
 * @author Xinzhe
 * @since 2025-06-11
 */
public interface PetProductService extends IService<PetProduct> {

    PetProductVO getPetProductVo(PetProduct petProduct);
}
