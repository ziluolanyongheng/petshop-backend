package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetImageRel;
import com.petshop.petshop_backend.mapper.PetImageRelMapper;
import com.petshop.petshop_backend.service.PetImageRelService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.petshop.petshop_backend.entity.table.PetImageRelTableDef.PET_IMAGE_REL;


/**
 * 图片关联表 服务层实现。
 *
 * @author user
 * @since 2025-06-11
 */
@Service
public class PetImageRelServiceImpl extends ServiceImpl<PetImageRelMapper, PetImageRel> implements PetImageRelService {

    @Override
    public List<PetImageRel> getByEntityTypeAndEntityId(String entityType, Long entityId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(PET_IMAGE_REL)
            .where(PET_IMAGE_REL.ENTITY_TYPE.eq(entityType))
            .and(PET_IMAGE_REL.ENTITY_ID.eq(entityId))
            .orderBy(PET_IMAGE_REL.SORT_ORDER.asc());
        
        return this.list(queryWrapper);
    }
}
