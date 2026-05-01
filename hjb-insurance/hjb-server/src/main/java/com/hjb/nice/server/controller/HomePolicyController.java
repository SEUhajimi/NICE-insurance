package com.hjb.nice.server.controller;

import com.hjb.nice.entity.HomePolicy;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.HomePolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Admin - Home Policy Management", description = "CRUD operations for HomePolicy (requires EMPLOYEE Token)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/home-policies")
public class HomePolicyController {

    @Autowired
    private HomePolicyService homePolicyService;

    @GetMapping
    public Result<List<HomePolicy>> findAll() {
        return Result.success(homePolicyService.findAll());
    }

    @GetMapping("/{id}")
    public Result<HomePolicy> findById(@PathVariable("id") Integer id) {
        return Result.success(homePolicyService.findById(id));
    }

    @GetMapping("/customer/{custId}")
    public Result<List<HomePolicy>> findByCustomerId(@PathVariable("custId") Integer custId) {
        return Result.success(homePolicyService.findByCustomerId(custId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody HomePolicy homePolicy) {
        homePolicyService.add(homePolicy);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody HomePolicy homePolicy) {
        homePolicyService.update(homePolicy);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable("id") Integer id) {
        homePolicyService.deleteById(id);
        return Result.success();
    }
}
