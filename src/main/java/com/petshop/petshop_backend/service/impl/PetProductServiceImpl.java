package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.controller.OssController;
import com.petshop.petshop_backend.entity.PetProduct;
import com.petshop.petshop_backend.entity.vo.PetProductVO;
import com.petshop.petshop_backend.entity.vo.PetStoreVO;
import com.petshop.petshop_backend.mapper.PetProductMapper;
import com.petshop.petshop_backend.service.PetProductService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  服务层实现。
 *
 * @author Xinzhe
 * @since 2025-06-11
 */
@Service
public class PetProductServiceImpl extends ServiceImpl<PetProductMapper, PetProduct> implements PetProductService {

    @Autowired
    private PetProductMapper petProductMapper;

    @Autowired
    private OssController ossController;

    @Override
    public PetProductVO getPetProductVo(PetProduct petProduct) {
        PetProductVO petProductVO = new PetProductVO();
        BeanUtils.copyProperties(petProduct, petProductVO);
        List<String> imageUrl = ossController.getImageUrl("product", (long) petProduct.getProductId());
        petProductVO.setProductUrls(imageUrl);

        return petProductVO;
    }
}
