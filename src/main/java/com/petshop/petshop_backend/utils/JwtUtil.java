package com.petshop.petshop_backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.petshop.petshop_backend.entity.PetUser;


import java.util.Calendar;
import java.util.Date;

public class JwtUtil {
  // 定义加密函数，参数为随机字符串用于加密
  private static final Algorithm algorithm = Algorithm.HMAC256("2dozl28%$%3=b3()ndka9duf==*");

  // 解密Token
  static public Object parseToken(String token, String key) {
      JWTVerifier jwtVerifier = JWT.require(algorithm).build();
      return jwtVerifier.verify(token).getClaim(key).as(Object.class);
  }
    static public String generateToken(PetUser petUser) {
        Date start = new Date();
        Calendar calc = Calendar.getInstance();
        calc.add(Calendar.DATE, 1);
        Date end = calc.getTime(); // 截止日期
        return JWT.create()
                // 两个withClaim声明Payload放置的内容
                .withClaim("id", petUser.getUserId())
                .withClaim("username", petUser.getUserName())
                .withIssuedAt(start)
                .withExpiresAt(end)
                // 用定义的加密函数签名
                .sign(algorithm);
 }
  //...
}