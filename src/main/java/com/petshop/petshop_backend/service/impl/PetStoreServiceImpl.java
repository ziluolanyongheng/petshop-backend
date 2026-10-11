package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetStore;
import com.petshop.petshop_backend.mapper.PetStoreMapper;
import com.petshop.petshop_backend.service.PetStoreService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author Xinzhe
 * @since 2025-06-11
 */
@Service
public class PetStoreServiceImpl extends ServiceImpl<PetStoreMapper, PetStore> implements PetStoreService {

}
