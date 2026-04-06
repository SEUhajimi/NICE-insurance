package com.hjb.nice.server.controller.auth;

import com.hjb.nice.dto.CustomerRegisterRequest;
import com.hjb.nice.dto.LoginRequest;
import com.hjb.nice.dto.LoginResponse;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证", description = "登录与注册（无需 Token）")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "员工登录", description = "返回 JWT Token，角色为 EMPLOYEE")
    @PostMapping("/employee/login")
    public Result<LoginResponse> employeeLogin(@RequestBody LoginRequest request) {
        return Result.success(authService.employeeLogin(request));
    }

    @Operation(summary = "客户登录", description = "返回 JWT Token，角色为 CUSTOMER")
    @PostMapping("/customer/login")
    public Result<LoginResponse> customerLogin(@RequestBody LoginRequest request) {
        return Result.success(authService.customerLogin(request));
    }

    @Operation(summary = "客户注册", description = "同时创建 Customer 记录和账号")
    @PostMapping("/customer/register")
    public Result<Void> customerRegister(@RequestBody CustomerRegisterRequest request) {
        authService.customerRegister(request);
        return Result.success();
    }
}
