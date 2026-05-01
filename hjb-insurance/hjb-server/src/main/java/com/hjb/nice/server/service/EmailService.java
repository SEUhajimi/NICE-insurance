package com.hjb.nice.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtp(String toEmail, String otp) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
            "from", "HJB Insurance <noreply@hhlnyu.space>",
            "to", new String[]{toEmail},
            "subject", "HJB Insurance — Password Reset Code",
            "text", "Your password reset code is: " + otp +
                    "\n\nThis code expires in 10 minutes." +
                    "\nIf you did not request a password reset, please ignore this email."
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
    }
}
