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
 * Automatically creates the default admin account on application startup (if it does not exist).
 * The password is read from the ADMIN_PASSWORD environment variable; falls back to a default value for development only.
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
            log.warn("Default admin account created. Please set a secure password via the ADMIN_PASSWORD environment variable.");
        }
    }
}
