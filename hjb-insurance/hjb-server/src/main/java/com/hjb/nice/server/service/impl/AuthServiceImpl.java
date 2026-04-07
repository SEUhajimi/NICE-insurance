package com.hjb.nice.server.service.impl;

import com.hjb.nice.dto.CustomerRegisterRequest;
import com.hjb.nice.dto.LoginRequest;
import com.hjb.nice.dto.LoginResponse;
import com.hjb.nice.dto.ResetPasswordRequest;
import com.hjb.nice.entity.Customer;
import com.hjb.nice.entity.CustomerAccount;
import com.hjb.nice.entity.Employee;
import com.hjb.nice.server.mapper.CustomerAccountMapper;
import com.hjb.nice.server.mapper.CustomerMapper;
import com.hjb.nice.server.mapper.EmployeeMapper;
import com.hjb.nice.server.service.AuthService;
import com.hjb.nice.server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private CustomerAccountMapper customerAccountMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse employeeLogin(LoginRequest request) {
        Employee employee = employeeMapper.findByUsername(request.getUsername());
        if (employee == null || !passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(employee.getUsername(), "EMPLOYEE");
        return new LoginResponse(token, "EMPLOYEE", employee.getUsername());
    }

    @Override
    public LoginResponse customerLogin(LoginRequest request) {
        CustomerAccount account = customerAccountMapper.findByUsername(request.getUsername());
        if (account == null || !passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(account.getUsername(), "CUSTOMER");
        return new LoginResponse(token, "CUSTOMER", account.getUsername());
    }

    @Override
    @Transactional
    public void customerRegister(CustomerRegisterRequest request) {
        if (customerAccountMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (customerAccountMapper.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 只创建账号，个人信息暂存于此，下单时才创建 hjb_customer 记录
        CustomerAccount account = new CustomerAccount();
        account.setCustomerId(null);
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        account.setFname(request.getFname());
        account.setLname(request.getLname());
        account.setGender(request.getGender());
        account.setMaritalStatus(request.getMaritalStatus());
        account.setAddrStreet(request.getAddrStreet());
        account.setAddrCity(request.getAddrCity());
        account.setAddrState(request.getAddrState());
        account.setZipcode(request.getZipcode());
        customerAccountMapper.insert(account);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        CustomerAccount account = customerAccountMapper.findByUsername(request.getUsername());
        if (account == null || !account.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new RuntimeException("用户名或邮箱不匹配");
        }
        customerAccountMapper.updatePassword(account.getAccountId(), passwordEncoder.encode(request.getNewPassword()));
    }
}
