package com.hjb.nice.server.service;

import com.hjb.nice.dto.CustomerRegisterRequest;
import com.hjb.nice.dto.LoginRequest;
import com.hjb.nice.dto.LoginResponse;

public interface AuthService {

    LoginResponse employeeLogin(LoginRequest request);

    LoginResponse customerLogin(LoginRequest request);

    void customerRegister(CustomerRegisterRequest request);
}
