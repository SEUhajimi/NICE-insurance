package com.hjb.nice.server.config;

import com.hjb.nice.entity.Employee;
import com.hjb.nice.server.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动时自动创建默认 admin 账号（若不存在）
 * 默认账号: admin / Admin@123
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (employeeMapper.findByUsername("admin") == null) {
            Employee admin = new Employee();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setEmail("admin@hjbinsurance.com");
            admin.setFname("Admin");
            admin.setLname("User");
            admin.setRole("EMPLOYEE");
            employeeMapper.insert(admin);
            System.out.println(">>> 默认管理员账号已创建: admin / Admin@123  （生产环境请立即修改密码）");
        }
    }
}
