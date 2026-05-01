package com.hjb.nice.server.controller;

import com.hjb.nice.entity.Driver;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.DriverService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Admin - Driver Management", description = "CRUD operations for Driver (requires EMPLOYEE Token)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping
    public Result<List<Driver>> findAll() {
        return Result.success(driverService.findAll());
    }

    @GetMapping("/{license}")
    public Result<Driver> findById(@PathVariable("license") String license) {
        return Result.success(driverService.findById(license));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Driver driver) {
        driverService.add(driver);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Driver driver) {
        driverService.update(driver);
        return Result.success();
    }

    @DeleteMapping("/{license}")
    public Result<Void> deleteById(@PathVariable("license") String license) {
        driverService.deleteById(license);
        return Result.success();
    }
}
