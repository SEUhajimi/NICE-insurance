package com.hjb.nice.server.service.impl;

import com.hjb.nice.dto.CustomerRegisterRequest;
import com.hjb.nice.dto.LoginRequest;
import com.hjb.nice.dto.LoginResponse;
import com.hjb.nice.dto.ResetPasswordRequest;
import com.hjb.nice.dto.SendOtpRequest;
import com.hjb.nice.entity.Customer;
import com.hjb.nice.entity.CustomerAccount;
import com.hjb.nice.entity.Employee;
import com.hjb.nice.server.mapper.CustomerAccountMapper;
import com.hjb.nice.server.mapper.CustomerMapper;
import com.hjb.nice.server.mapper.EmployeeMapper;
import com.hjb.nice.server.service.AuthService;
import com.hjb.nice.server.service.EmailService;
import com.hjb.nice.server.service.OtpStore;
import com.hjb.nice.server.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

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

    @Autowired
    private OtpStore otpStore;

    @Autowired
    private EmailService emailService;

    @Override
    public LoginResponse employeeLogin(LoginRequest request) {
        Employee employee = employeeMapper.findByUsername(request.getUsername());
        if (employee == null || !passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        String token = jwtUtil.generateToken(employee.getUsername(), "EMPLOYEE");
        return new LoginResponse(token, "EMPLOYEE", employee.getUsername());
    }

    @Override
    public LoginResponse customerLogin(LoginRequest request) {
        CustomerAccount account = customerAccountMapper.findByUsername(request.getUsername());
        if (account == null || !passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        String token = jwtUtil.generateToken(account.getUsername(), "CUSTOMER");
        return new LoginResponse(token, "CUSTOMER", account.getUsername());
    }

    @Override
    @Transactional
    public void customerRegister(CustomerRegisterRequest request) {
        if (customerAccountMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("This username is not available");
        }
        if (customerAccountMapper.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("This email is not available");
        }

        // Only create the account; personal info is stored here and synced to hjb_customer on first purchase
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
    public void sendResetOtp(SendOtpRequest request) {
        CustomerAccount account = customerAccountMapper.findByUsername(request.getUsername());
        if (account == null || !account.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Username or email does not match");
        }
        String code = otpStore.generate(request.getEmail());
        try {
            emailService.sendOtp(request.getEmail(), code);
        } catch (Exception e) {
            log.error("OTP email failed: {}", e.getMessage());
            throw new RuntimeException("Failed to send verification code. Please try again later.");
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        CustomerAccount account = customerAccountMapper.findByUsername(request.getUsername());
        if (account == null || !account.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Username or email does not match");
        }
        if (!otpStore.verify(request.getEmail(), request.getOtp())) {
            throw new RuntimeException("Verification code is invalid or has expired");
        }
        customerAccountMapper.updatePassword(account.getAccountId(), passwordEncoder.encode(request.getNewPassword()));
    }
}
