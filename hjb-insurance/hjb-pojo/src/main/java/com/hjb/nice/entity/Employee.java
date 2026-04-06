package com.hjb.nice.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Employee {
    private Integer empId;
    private String username;
    private String password;
    private String email;
    private String fname;
    private String lname;
    private String role;         // EMPLOYEE 或 ADMIN
    private LocalDateTime createdAt;
}
