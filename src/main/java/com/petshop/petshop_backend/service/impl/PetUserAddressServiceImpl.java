package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetUserAddress;
import com.petshop.petshop_backend.mapper.PetUserAddressMapper;
import com.petshop.petshop_backend.service.PetUserAddressService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author Xinzhe
 * @since 2025-06-11
 */
@Service
public class PetUserAddressServiceImpl extends ServiceImpl<PetUserAddressMapper, PetUserAddress> implements PetUserAddressService {

}
