package com.petshop.petshop_backend.service;

import com.mybatisflex.core.service.IService;
import com.petshop.petshop_backend.entity.PetImageRel;

import java.util.List;

/**
 * 图片关联表 服务层。
 *
 * @author user
 * @since 2025-06-11
 */
public interface PetImageRelService extends IService<PetImageRel> {
    /**
     * 通过实体类型和ID获取图片关联记录
     *
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @return 图片关联记录
     */
   List<PetImageRel> getByEntityTypeAndEntityId(String entityType, Long entityId);
}
