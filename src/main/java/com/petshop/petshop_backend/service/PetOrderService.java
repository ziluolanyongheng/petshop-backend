package com.petshop.petshop_backend.service;

import com.mybatisflex.core.service.IService;
import com.petshop.petshop_backend.entity.PetOrder;
import com.petshop.petshop_backend.entity.PetOrderItem;
import com.petshop.petshop_backend.entity.vo.PetOrderItemVO;

/**
 * 订单表 服务层。
 *
 * @author 14020
 * @since 2025-06-24
 */
public interface PetOrderService extends IService<PetOrder> {
    PetOrderItemVO getOrderItemDetail(PetOrderItem petOrderItem);
}
