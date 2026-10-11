package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;

import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.base.LoginUser;
import com.petshop.petshop_backend.mapper.PetUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.petshop.petshop_backend.entity.table.PetUserTableDef.PET_USER;

//import static com.petshop.petshop_backend.entity.table.UsersTableDef.PETUSER;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PetUserMapper petUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper wrapper = new QueryWrapper();


//        USERS.USERNAME.eq(username)
        wrapper.where(PET_USER.USER_NAME.eq(username));
        PetUser petUser = petUserMapper.selectOneByQuery(wrapper);
        if( petUser == null ){
            return null;
        }

        return new LoginUser(petUser);
    }
}
