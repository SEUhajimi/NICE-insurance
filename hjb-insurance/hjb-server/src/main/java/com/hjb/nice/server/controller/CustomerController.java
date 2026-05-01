package com.hjb.nice.server.controller;

import com.hjb.nice.entity.Customer;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Customer Management", description = "CRUD operations for customer information (requires EMPLOYEE Token)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "Get all customers")
    @GetMapping
    public Result<List<Customer>> findAll() {
        return Result.success(customerService.findAll());
    }

    @Operation(summary = "Get customer by ID")
    @GetMapping("/{id}")
    public Result<Customer> findById(@PathVariable("id") Integer id) {
        return Result.success(customerService.findById(id));
    }

    @Operation(summary = "Create a new customer")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody Customer customer) {
        customerService.add(customer);
        return Result.success();
    }

    @Operation(summary = "Update customer information")
    @PutMapping
    public Result<Void> update(@Validated @RequestBody Customer customer) {
        customerService.update(customer);
        return Result.success();
    }

    @Operation(summary = "Delete a customer")
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable("id") Integer id) {
        customerService.deleteById(id);
        return Result.success();
    }
}
