package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.dto.AuthParamsDTO;
import com.petshop.petshop_backend.service.AuthService;
import com.petshop.petshop_backend.service.PetUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {

    @Autowired
    private PetUserService petUserService;

    @Override
    public PetUser execute(AuthParamsDTO authParamsDTO) {

        String username = authParamsDTO.getUsername();
        String password = authParamsDTO.getPassword();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PetUser::getUserName, username);

        PetUser petUser = petUserService.getOne(queryWrapper);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(password, petUser.getUserPassword());

        if(!matches){
            return null;
        }

        return petUser;
    }
}
