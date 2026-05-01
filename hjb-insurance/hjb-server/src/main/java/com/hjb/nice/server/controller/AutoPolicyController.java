package com.hjb.nice.server.controller;

import com.hjb.nice.entity.AutoPolicy;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.AutoPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Admin - Auto Policy Management", description = "CRUD operations for AutoPolicy (requires EMPLOYEE Token)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/auto-policies")
public class AutoPolicyController {

    @Autowired
    private AutoPolicyService autoPolicyService;

    @GetMapping
    public Result<List<AutoPolicy>> findAll() {
        return Result.success(autoPolicyService.findAll());
    }

    @GetMapping("/{id}")
    public Result<AutoPolicy> findById(@PathVariable("id") Integer id) {
        return Result.success(autoPolicyService.findById(id));
    }

    @GetMapping("/customer/{custId}")
    public Result<List<AutoPolicy>> findByCustomerId(@PathVariable("custId") Integer custId) {
        return Result.success(autoPolicyService.findByCustomerId(custId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody AutoPolicy autoPolicy) {
        autoPolicyService.add(autoPolicy);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody AutoPolicy autoPolicy) {
        autoPolicyService.update(autoPolicy);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable("id") Integer id) {
        autoPolicyService.deleteById(id);
        return Result.success();
    }
}
