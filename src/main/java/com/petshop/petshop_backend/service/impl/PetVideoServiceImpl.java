package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetVideo;
import com.petshop.petshop_backend.entity.PetVideoStats;
import com.petshop.petshop_backend.entity.vo.PetVideoStatsVO;
import com.petshop.petshop_backend.mapper.PetVideoMapper;
import com.petshop.petshop_backend.service.PetVideoService;
import com.petshop.petshop_backend.service.PetVideoStatsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 14020
 * @since 2025-06-30
 */
@Service
public class PetVideoServiceImpl extends ServiceImpl<PetVideoMapper, PetVideo> implements PetVideoService {

}
