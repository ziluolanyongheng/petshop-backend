package com.petshop.petshop_backend.exception;


import com.petshop.petshop_backend.entity.base.Result;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    // .....
    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseBody
    public Result loginFailed(BadCredentialsException e){
        return Result.loginFail();
    }


    @ExceptionHandler(value = InsufficientAuthenticationException.class)
    @ResponseBody
    public Result AuthenticationException(InsufficientAuthenticationException e){
        return Result.loginFail();
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exception(Exception e){
        return Result.error("发生错误："+e.getMessage());
    }
    //....
}