package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetImage;
import com.petshop.petshop_backend.mapper.PetImageMapper;
import com.petshop.petshop_backend.service.PetImageService;
import org.springframework.stereotype.Service;

/**
 * 图片资源表 服务层实现。
 *
 * @author user
 * @since 2025-06-11
 */
@Service
public class PetImageServiceImpl extends ServiceImpl<PetImageMapper, PetImage> implements PetImageService {

}
