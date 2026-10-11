package com.petshop.petshop_backend.service;




public interface VerificationService {
    void sendEmail(String email,String message,String subject);
}
