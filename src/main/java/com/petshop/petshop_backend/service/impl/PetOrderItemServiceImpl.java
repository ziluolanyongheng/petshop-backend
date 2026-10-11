package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetOrderItem;
import com.petshop.petshop_backend.mapper.PetOrderItemMapper;
import com.petshop.petshop_backend.service.PetOrderItemService;
import org.springframework.stereotype.Service;

/**
 * 订单商品表 服务层实现。
 *
 * @author 14020
 * @since 2025-06-24
 */
@Service
public class PetOrderItemServiceImpl extends ServiceImpl<PetOrderItemMapper, PetOrderItem> implements PetOrderItemService {

}
