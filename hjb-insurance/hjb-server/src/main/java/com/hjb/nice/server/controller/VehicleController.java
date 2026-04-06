package com.hjb.nice.server.controller;

import com.hjb.nice.entity.Vehicle;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.VehicleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "管理端 - 车辆管理", description = "Vehicle的增删改查（需 EMPLOYEE Token）")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public Result<List<Vehicle>> findAll() {
        return Result.success(vehicleService.findAll());
    }

    @GetMapping("/{vin}")
    public Result<Vehicle> findById(@PathVariable("vin") String vin) {
        return Result.success(vehicleService.findById(vin));
    }

    @GetMapping("/policy/{apId}")
    public Result<List<Vehicle>> findByAutoPolicyId(@PathVariable("apId") Integer apId) {
        return Result.success(vehicleService.findByAutoPolicyId(apId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Vehicle vehicle) {
        vehicleService.add(vehicle);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Vehicle vehicle) {
        vehicleService.update(vehicle);
        return Result.success();
    }

    @DeleteMapping("/{vin}")
    public Result<Void> deleteById(@PathVariable("vin") String vin) {
        vehicleService.deleteById(vin);
        return Result.success();
    }
}
