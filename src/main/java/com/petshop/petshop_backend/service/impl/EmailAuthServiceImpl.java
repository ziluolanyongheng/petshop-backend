package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.PetUserAuth;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.entity.dto.AuthParamsDTO;
import com.petshop.petshop_backend.mapper.PetUserMapper;
import com.petshop.petshop_backend.service.AuthService;
import com.petshop.petshop_backend.service.PetUserAuthService;
import com.petshop.petshop_backend.service.PetUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("email_authservice")
public class EmailAuthServiceImpl implements AuthService {

    @Autowired
    private PetUserService petUserService;

    @Autowired
    private PetUserAuthService petUserAuthService;

    @Override
    public PetUser execute(AuthParamsDTO authParamsDTO) throws Exception {

        String email = authParamsDTO.getEmail();
        String checkcode = authParamsDTO.getCheckcode();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PetUser::getUserEmail, email);

        PetUser petUser = petUserService.getOne(queryWrapper);
        if(petUser == null) {
            throw new Exception("邮箱不存在，请注册");
        }


        QueryWrapper queryWrapper2 = new QueryWrapper().eq(PetUserAuth::getUserEmail, email).eq(PetUserAuth::getAuthType, "email");
        PetUserAuth auth = petUserAuthService.getOne(queryWrapper2);
        if(auth == null){//若不存在新建
            throw new Exception("邮箱不存在");
        }else{
            if(auth.getAuthToken().equals(checkcode)){
                return petUser;
            }else{
                throw new Exception("验证失败");
            }
        }


    }
}
