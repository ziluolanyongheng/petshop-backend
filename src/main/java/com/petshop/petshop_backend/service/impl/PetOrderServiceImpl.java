package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.*;
import com.petshop.petshop_backend.entity.vo.PetOrderItemVO;
import com.petshop.petshop_backend.mapper.PetOrderMapper;
import com.petshop.petshop_backend.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单表 服务层实现。
 *
 * @author 14020
 * @since 2025-06-24
 */
@Service
public class PetOrderServiceImpl extends ServiceImpl<PetOrderMapper, PetOrder> implements PetOrderService {
    @Autowired
    private PetProductService petProductService;
    @Autowired
    private PetStoreService petStoreService;
    @Autowired
    private PetImageRelService petImageRelService;

    @Autowired
    private PetImageService petImageService;
    @Override

    //获取orderItem详细
    public PetOrderItemVO getOrderItemDetail(PetOrderItem petOrderItem) {
        PetOrderItemVO petOrderItemVO = new PetOrderItemVO();
        BeanUtils.copyProperties(petOrderItem, petOrderItemVO);


        //查询商品
        PetProduct petProduct = petProductService.getById(petOrderItem.getProductId());
        //查询商店
        PetStore petStore = petStoreService.getById(petOrderItem.getStoreId());

        BeanUtils.copyProperties(petOrderItem, petOrderItemVO);

        petOrderItemVO.setStoreName(petStore.getStoreName());

        List<PetImageRel> storeImgs = petImageRelService.getByEntityTypeAndEntityId("store", petStore.getStoreId());
        PetImage petImage = petImageService.getById(storeImgs.get(0).getImgId());
        petOrderItemVO.setStoreImg(petImage.getImgUrl());
        petOrderItemVO.setProductName(petProduct.getProductName());
        petOrderItemVO.setProductThumb(petProduct.getProductThumb());

        return petOrderItemVO;
    }
}
