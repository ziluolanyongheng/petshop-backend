package com.petshop.petshop_backend.service.impl;

import com.petshop.petshop_backend.service.VerificationService;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class VerificationServiceImpl implements VerificationService {


    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String email,String message,String subject) {


        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(email));
                mimeMessage.setSubject(subject);
                mimeMessage.setFrom(new InternetAddress("yyanyy2025@163.com"));
                mimeMessage.setText(message);
            }
        };

        try {
            this.mailSender.send(preparator);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
}
