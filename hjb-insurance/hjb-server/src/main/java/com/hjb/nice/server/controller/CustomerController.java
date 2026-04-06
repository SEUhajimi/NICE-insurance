package com.hjb.nice.server.controller;

import com.hjb.nice.entity.Customer;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理端 - 客户管理", description = "客户信息的增删改查（需 EMPLOYEE Token）")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "查询所有客户")
    @GetMapping
    public Result<List<Customer>> findAll() {
        return Result.success(customerService.findAll());
    }

    @Operation(summary = "根据ID查询客户")
    @GetMapping("/{id}")
    public Result<Customer> findById(@PathVariable("id") Integer id) {
        return Result.success(customerService.findById(id));
    }

    @Operation(summary = "新增客户")
    @PostMapping
    public Result<Void> add(@RequestBody Customer customer) {
        customerService.add(customer);
        return Result.success();
    }

    @Operation(summary = "修改客户信息")
    @PutMapping
    public Result<Void> update(@RequestBody Customer customer) {
        customerService.update(customer);
        return Result.success();
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable("id") Integer id) {
        customerService.deleteById(id);
        return Result.success();
    }
}
