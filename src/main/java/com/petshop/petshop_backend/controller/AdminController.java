package com.petshop.petshop_backend.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.service.PetUserService;
import com.petshop.petshop_backend.utils.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "10 管理员管理接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PetUserService petUserService;

    //登录接口
    @Operation(summary = "获取全部用户列表")
    @GetMapping("/all")
    @ApiOperationSupport(order = 10)
    public Result getAllUser() {

        Integer currentId = BaseContext.getCurrentId();

        //获取用户信息
        PetUser user = petUserService.getById(currentId);
        if(user.getUserRole()!= 2){
            return Result.error("只允许管理员请求");
        }

        List<PetUser> petUsers = petUserService.list();
        return Result.success(petUsers);

    }
}
