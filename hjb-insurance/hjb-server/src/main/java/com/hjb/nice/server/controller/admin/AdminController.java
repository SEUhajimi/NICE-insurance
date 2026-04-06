package com.hjb.nice.server.controller.admin;

import com.hjb.nice.entity.Employee;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.mapper.EmployeeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理端 - 员工管理", description = "员工账号的增删查（需 EMPLOYEE Token）")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/admin/employees")
public class AdminController {


    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Operation(summary = "查询所有员工")
    @GetMapping
    public Result<List<Employee>> findAll() {
        List<Employee> employees = employeeMapper.findAll();
        // 不返回密码字段
        employees.forEach(e -> e.setPassword(null));
        return Result.success(employees);
    }

    @Operation(summary = "新增员工账号")
    @PostMapping
    public Result<Void> addEmployee(@RequestBody Employee employee) {
        if (employeeMapper.findByUsername(employee.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        if (employee.getRole() == null) {
            employee.setRole("EMPLOYEE");
        }
        employeeMapper.insert(employee);
        return Result.success();
    }

    @Operation(summary = "删除员工账号")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEmployee(@PathVariable Integer id) {
        employeeMapper.deleteById(id);
        return Result.success();
    }
}
