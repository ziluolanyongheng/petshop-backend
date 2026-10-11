package com.petshop.petshop_backend.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.entity.PetUserAuth;
import com.petshop.petshop_backend.entity.base.Result;

import com.petshop.petshop_backend.service.PetUserAuthService;
import com.petshop.petshop_backend.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/verification")
@Tag(name = "5 校验接口")
@Slf4j
public class VerificationController {
    //

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private PetUserAuthService petUserAuthService;

    @PostMapping("/sendEmailCode")
    @ApiOperationSupport(order = 10)
    @Operation(summary = "发送邮箱验证码接口")

    public Result sendEmailCode(@RequestParam String email) {

        //生成随机验证码
        int code = new Random().nextInt(9000) + 1000; // 1000-9999

//        将验证码插入到校验Auth表中
        QueryWrapper queryWrapper = new QueryWrapper().eq(PetUserAuth::getUserEmail, email);
        PetUserAuth auth = petUserAuthService.getOne(queryWrapper);

        if(auth == null){//若不存在新建
            auth = new PetUserAuth();
            auth.setUserEmail(email);
            auth.setAuthToken(String.valueOf(code));
            auth.setCreatedTime(LocalDateTime.now());
            auth.setAuthType("email");
            petUserAuthService.save(auth);
        }else{
            auth.setAuthType("email");
            auth.setAuthToken(String.valueOf(code));
            petUserAuthService.updateById(auth);
        }


        String message = "【厦理宠物】欢迎使用TungTungTungSahur小组开发的宠物商店，您的验证码是:"+ code+ "。如非本人操作，请检查账号安全";
        verificationService.sendEmail(email,message,"厦理宠物验证码");
        log.info(email+"验证码邮件发送成功");
        return Result.success("发送成功");
    }


    @PostMapping("/verifyEmailCode")
    @ApiOperationSupport(order = 11)
    @Operation(summary = "验证邮箱验证码接口")
    public Result verifyEmailCode(@RequestParam String email,@RequestParam String code) {


        QueryWrapper queryWrapper = new QueryWrapper().eq(PetUserAuth::getUserEmail, email).eq(PetUserAuth::getAuthType, "email");
        PetUserAuth auth = petUserAuthService.getOne(queryWrapper);
        if(auth == null){//若不存在新建
            return Result.error("邮箱不存在");
        }else{
            if(auth.getAuthToken().equals(code)){
                return Result.success("验证成功！");
            }else{
                return Result.error("验证失败");
            }
        }
    }
}
