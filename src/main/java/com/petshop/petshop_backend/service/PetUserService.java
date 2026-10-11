package com.petshop.petshop_backend.service;

import com.mybatisflex.core.service.IService;
import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.dto.PetUserRegisterDTO;
import com.petshop.petshop_backend.entity.dto.PushMessagesDTO;

/**
 * 用户表 服务层。
 *
 * @author Xinzhe
 * @since 2025-06-09
 */
public interface PetUserService extends IService<PetUser> {
    public Object login(PetUser petUser);

    Object register(PetUserRegisterDTO  petUserRegisterDTO);


    void pushMessage(PushMessagesDTO pushMessagesDTO);
}
