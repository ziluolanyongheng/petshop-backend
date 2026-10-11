package com.petshop.petshop_backend.entity.base;

import lombok.Data;

@Data
public class Result<T> {
    //成功
    public static final Integer SUCCESS=100;
    //失败
    public static final Integer ERROR=0;
    //登录失败
    public static final Integer LOGIN_FAIL=102;
    //...其他返回值类型

    Integer code;     // 返回值
    String  message;  // 错误信息
    T  data;     // 携带的数据

    public static<T> Result<T> success( T data ){
        Result<T> result = new Result<T>();
        result.code=SUCCESS;
        result.data=data;
        result.message="success";
        return result;
    }
    public static Result error( String message ) {
        Result result = new Result<>();
        result.code = ERROR;
        result.message = message;
        result.data = null;
        return result;
    }

    public static Result loginFail() {
        Result result = new Result<>();
        result.code = LOGIN_FAIL;
        result.message = "账号或密码错误";
        result.data = null;
        return result;
    }
    //.....其他函数
}