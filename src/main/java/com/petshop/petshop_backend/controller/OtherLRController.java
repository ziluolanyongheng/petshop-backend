package com.petshop.petshop_backend.controller;

import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.service.impl.QQAuthServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Tag(name = "9 第三方登录注册接口")
public class OtherLRController {

    @Autowired
    private QQAuthServiceImpl qqLoginService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    /**
     * 处理第三方QQ登录API的回调
     * 类似于PHP代码的功能
     */
    @GetMapping("/qq/callback-page")
    public String qqCallbackPage() {
        return "qq-callback";
    }

    @GetMapping("/qq/callback")
    @ResponseBody
    public Result qqCallback(@RequestParam(required = false) String code, 
                           @RequestParam(required = false) String msg) {
        
        if (code == null || code.isEmpty()) {
            return Result.error("错误：缺少授权码");
        }
        
        try {
            // 使用QQ登录服务处理登录
            Result result = qqLoginService.handleQQLogin(code);
            
            // 如果登录成功，返回用户信息（包含token）
            if (result.getCode() == 100) {
                return result;
            } else {
                return Result.error("QQ登录失败：" + result.getMessage());
            }
            
        } catch (Exception e) {
            return Result.error("QQ登录处理异常：" + e.getMessage());
        }
    }


}
