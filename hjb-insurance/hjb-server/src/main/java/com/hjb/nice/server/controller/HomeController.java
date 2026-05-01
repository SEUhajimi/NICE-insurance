package com.hjb.nice.server.controller;

import com.hjb.nice.entity.Home;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.HomeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Admin - Property Management", description = "CRUD operations for Home/Property (requires EMPLOYEE Token)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/homes")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping
    public Result<List<Home>> findAll() {
        return Result.success(homeService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Home> findById(@PathVariable("id") Integer id) {
        return Result.success(homeService.findById(id));
    }

    @GetMapping("/policy/{hpId}")
    public Result<List<Home>> findByHomePolicyId(@PathVariable("hpId") Integer hpId) {
        return Result.success(homeService.findByHomePolicyId(hpId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Home home) {
        homeService.add(home);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Home home) {
        homeService.update(home);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable("id") Integer id) {
        homeService.deleteById(id);
        return Result.success();
    }
}
