package com.hjb.nice.server.controller.auth;

import com.hjb.nice.dto.CustomerRegisterRequest;
import com.hjb.nice.dto.LoginRequest;
import com.hjb.nice.dto.LoginResponse;
import com.hjb.nice.dto.ResetPasswordRequest;
import com.hjb.nice.dto.SendOtpRequest;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Login and registration endpoints (no Token required)")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Employee login", description = "Returns a JWT Token with role EMPLOYEE")
    @PostMapping("/employee/login")
    public Result<LoginResponse> employeeLogin(@RequestBody LoginRequest request) {
        return Result.success(authService.employeeLogin(request));
    }

    @Operation(summary = "Customer login", description = "Returns a JWT Token with role CUSTOMER")
    @PostMapping("/customer/login")
    public Result<LoginResponse> customerLogin(@RequestBody LoginRequest request) {
        return Result.success(authService.customerLogin(request));
    }

    @Operation(summary = "Customer registration", description = "Creates both a Customer record and a portal account")
    @PostMapping("/customer/register")
    public Result<Void> customerRegister(@Valid @RequestBody CustomerRegisterRequest request) {
        authService.customerRegister(request);
        return Result.success();
    }

    @Operation(summary = "Send password reset code", description = "Validates username and email, then sends a 6-digit OTP to the email address")
    @PostMapping("/customer/send-otp")
    public Result<Void> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        authService.sendResetOtp(request);
        return Result.success();
    }

    @Operation(summary = "Customer password reset", description = "Resets the password after verifying the OTP")
    @PostMapping("/customer/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return Result.success();
    }
}
