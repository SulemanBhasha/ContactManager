package com.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    public Boolean sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("sulemanbhashadodmni@gmail.com");
            mailMessage.setTo(toEmail);
            mailMessage.setText(body);
            mailMessage.setSubject(subject);
            
            
            javaMailSender.send(mailMessage);
            System.out.println("Mail sent successfully");
            return true;
        } catch (MailException e) {
            e.printStackTrace();
            // Handle the error as needed, e.g., log the error
            return false;
        }
    }
}
