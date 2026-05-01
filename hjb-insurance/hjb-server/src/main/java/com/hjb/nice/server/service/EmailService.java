package com.hjb.nice.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject("HJB Insurance — Password Reset Code");
        msg.setText(
            "Your password reset code is: " + otp + "\n\n" +
            "This code expires in 10 minutes.\n" +
            "If you did not request a password reset, please ignore this email."
        );
        mailSender.send(msg);
    }
}
