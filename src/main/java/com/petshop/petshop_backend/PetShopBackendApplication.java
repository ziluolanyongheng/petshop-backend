package com.petshop.petshop_backend;

import com.petshop.petshop_backend.utils.EncryptorUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.petshop.petshop_backend.mapper")
@EnableScheduling //开启定时任务
public class PetShopBackendApplication {

    public static void main(String[] args) {


//        String origin = "12312312309182937498";
//        EncryptorUtil encryptorUtil = new EncryptorUtil("secret123");
//        try {
//            String encode = encryptorUtil.encrypt(origin);
//            System.out.println(encode);
//
//            String decrypt = encryptorUtil.decrypt(encode);
//            System.out.println(decrypt);
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


        SpringApplication.run(PetShopBackendApplication.class, args);


    }



}
