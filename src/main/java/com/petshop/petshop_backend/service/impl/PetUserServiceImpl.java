package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.PushMessages;
import com.petshop.petshop_backend.entity.base.LoginUser;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.entity.dto.PetUserRegisterDTO;
import com.petshop.petshop_backend.entity.dto.PushMessagesDTO;
import com.petshop.petshop_backend.entity.vo.PetUserVO;
import com.petshop.petshop_backend.mapper.PetUserMapper;
import com.petshop.petshop_backend.service.PetUserService;
import com.petshop.petshop_backend.service.PushMessagesService;
import com.petshop.petshop_backend.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.petshop.petshop_backend.entity.table.PetUserTableDef.PET_USER;

/**
 * 用户表 服务层实现。
 *
 * @author Xinzhe
 * @since 2025-06-09
 */
@Service
public class PetUserServiceImpl extends ServiceImpl<PetUserMapper, PetUser> implements PetUserService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PetUserMapper  petUserMapper;
    @Autowired
    PushMessagesService   pushMessagesService;

    @Override
    public Object login(PetUser petUser) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(petUser.getUserName(), petUser.getUserPassword());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(petUser.getUserPassword());

        Authentication auth = authenticationManager.authenticate(authenticationToken);



        LoginUser loginUser =(LoginUser) auth.getPrincipal();
        petUser = loginUser.getPetUser();


        PetUserVO petUserVO = new PetUserVO();

        BeanUtils.copyProperties(petUser, petUserVO);
//        petUserVO.setUserType(user.getUserType());
        petUserVO.setToken(JwtUtil.generateToken(petUser));
        return Result.success(petUserVO);
    }

    @Override
    public Object register(PetUserRegisterDTO petUserRegisterDTO) {
        //将密码加密后插入用户表
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(petUserRegisterDTO.getUserPassword());

        //创建数据库用户，填入数据
        PetUser petUser = new PetUser();
        BeanUtils.copyProperties(petUserRegisterDTO, petUser);
        petUser.setUserPassword(encode);

        petUser.setCreatedTime(LocalDateTime.now());
        petUser.setUpdatedTime(LocalDateTime.now());
        int i = petUserMapper.insert(petUser);


        //返回数据
        PetUserVO petUserVO = new PetUserVO();
        BeanUtils.copyProperties(petUser, petUserVO);
        petUserVO.setToken(JwtUtil.generateToken(petUser));

        return Result.success(petUserVO);
    }

    @Override
    public void pushMessage(PushMessagesDTO pushMessagesDTO) {
        PushMessages pushMessages = new PushMessages();
        BeanUtils.copyProperties(pushMessagesDTO, pushMessages);

        pushMessages.setIsRead(false);
        pushMessages.setCreatedAt(LocalDateTime.now());
        pushMessages.setUpdatedAt(LocalDateTime.now());

        if(pushMessages.getUserId() == -1){//全体发布
            ArrayList<PushMessages> pushMessagesArrayList = new ArrayList<>();
            List<PetUser> petUsers = this.list(new QueryWrapper().where(PET_USER.USER_ROLE.eq(0)));
            for (PetUser petUser : petUsers) {
                PushMessages messages = new PushMessages();
                BeanUtils.copyProperties(pushMessages, messages);
                messages.setUserId(petUser.getUserId());
                pushMessagesArrayList.add(messages);
            }
            pushMessagesService.saveBatch(pushMessagesArrayList);
        }else{
            pushMessagesService.save(pushMessages);
        }
    }
}
