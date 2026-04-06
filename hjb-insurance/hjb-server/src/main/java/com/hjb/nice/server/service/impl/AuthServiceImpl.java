package com.hjb.nice.server.service.impl;

import com.hjb.nice.dto.CustomerRegisterRequest;
import com.hjb.nice.dto.LoginRequest;
import com.hjb.nice.dto.LoginResponse;
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

        // 1. 创建 Customer 记录
        Customer customer = new Customer();
        customer.setFname(request.getFname());
        customer.setLname(request.getLname());
        customer.setGender(request.getGender());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setCustType(request.getCustType());
        customer.setAddrStreet(request.getAddrStreet());
        customer.setAddrCity(request.getAddrCity());
        customer.setAddrState(request.getAddrState());
        customer.setZipcode(request.getZipcode());
        customerMapper.insertForRegister(customer);  // custId 由数据库生成并回填

        // 2. 创建 CustomerAccount 记录（密码 BCrypt 加密）
        CustomerAccount account = new CustomerAccount();
        account.setCustomerId(customer.getCustId());
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        customerAccountMapper.insert(account);
    }
}
