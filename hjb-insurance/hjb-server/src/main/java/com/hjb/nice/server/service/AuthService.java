package com.hjb.nice.server.service;

import com.hjb.nice.dto.CustomerRegisterRequest;
import com.hjb.nice.dto.LoginRequest;
import com.hjb.nice.dto.LoginResponse;
import com.hjb.nice.dto.ResetPasswordRequest;
import com.hjb.nice.dto.SendOtpRequest;

public interface AuthService {

    LoginResponse employeeLogin(LoginRequest request);

    LoginResponse customerLogin(LoginRequest request);

    void customerRegister(CustomerRegisterRequest request);

    void sendResetOtp(SendOtpRequest request);

    void resetPassword(ResetPasswordRequest request);
}
