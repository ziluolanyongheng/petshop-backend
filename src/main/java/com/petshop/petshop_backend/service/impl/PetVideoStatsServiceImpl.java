package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetVideoStats;
import com.petshop.petshop_backend.mapper.PetVideoStatsMapper;
import com.petshop.petshop_backend.service.PetVideoStatsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  服务层实现。
 *
 * @author 14020
 * @since 2025-06-30
 */
@Service
public class PetVideoStatsServiceImpl extends ServiceImpl<PetVideoStatsMapper, PetVideoStats> implements PetVideoStatsService {
}
