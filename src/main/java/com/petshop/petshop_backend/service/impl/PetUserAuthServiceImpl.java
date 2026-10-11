package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetUserAuth;
import com.petshop.petshop_backend.mapper.PetUserAuthMapper;
import com.petshop.petshop_backend.service.PetUserAuthService;
import org.springframework.stereotype.Service;

/**
 * 用户认证表 服务层实现。
 *
 * @author Xinzhe
 * @since 2025-06-12
 */
@Service
public class PetUserAuthServiceImpl extends ServiceImpl<PetUserAuthMapper, PetUserAuth> implements PetUserAuthService {

}
