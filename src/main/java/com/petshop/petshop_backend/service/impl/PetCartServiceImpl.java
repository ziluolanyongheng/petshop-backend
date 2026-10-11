package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetCart;
import com.petshop.petshop_backend.mapper.PetCartMapper;
import com.petshop.petshop_backend.service.PetCartService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author Xinzhe
 * @since 2025-06-11
 */
@Service
public class PetCartServiceImpl extends ServiceImpl<PetCartMapper, PetCart> implements PetCartService {

}
