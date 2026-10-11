package com.petshop.petshop_backend.service;

import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.dto.AuthParamsDTO;

//统一登录接口
public interface AuthService {

    PetUser execute(AuthParamsDTO authParamsDTO) throws Exception;
}
