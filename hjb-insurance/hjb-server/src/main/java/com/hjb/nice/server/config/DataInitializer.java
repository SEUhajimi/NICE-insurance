package com.hjb.nice.server.config;

import com.hjb.nice.entity.Employee;
import com.hjb.nice.server.mapper.EmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动时自动创建默认 admin 账号（若不存在）。
 * 密码优先从环境变量 ADMIN_PASSWORD 读取，未设置时使用默认值（仅开发环境）。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.default-password:Admin@123}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) {
        if (employeeMapper.findByUsername("admin") == null) {
            Employee admin = new Employee();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
            admin.setEmail("admin@hjbinsurance.com");
            admin.setFname("Admin");
            admin.setLname("User");
            admin.setRole("EMPLOYEE");
            employeeMapper.insert(admin);
            log.warn("默认管理员账号已创建，请通过环境变量 ADMIN_PASSWORD 设置安全密码");
        }
    }
}
