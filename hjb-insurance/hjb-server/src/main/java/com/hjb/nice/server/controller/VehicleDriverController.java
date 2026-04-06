package com.hjb.nice.server.controller;

import com.hjb.nice.entity.VehicleDriver;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.mapper.VehicleDriverMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "管理端 - 车辆驾驶员关联", description = "VehicleDriver关联表的增删查（需 EMPLOYEE Token）")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/vehicle-drivers")
public class VehicleDriverController {

    @Autowired
    private VehicleDriverMapper vehicleDriverMapper;

    @GetMapping
    public Result<List<VehicleDriver>> findAll() {
        return Result.success(vehicleDriverMapper.findAll());
    }

    @GetMapping("/vehicle/{vin}")
    public Result<List<VehicleDriver>> findByVin(@PathVariable("vin") String vin) {
        return Result.success(vehicleDriverMapper.findByVin(vin));
    }

    @GetMapping("/driver/{license}")
    public Result<List<VehicleDriver>> findByDriverLicense(@PathVariable("license") String license) {
        return Result.success(vehicleDriverMapper.findByDriverLicense(license));
    }

    @PostMapping
    public Result<Void> add(@RequestBody VehicleDriver vd) {
        vehicleDriverMapper.insert(vd);
        return Result.success();
    }

    @DeleteMapping("/{vin}/{license}")
    public Result<Void> delete(@PathVariable("vin") String vin, @PathVariable("license") String license) {
        vehicleDriverMapper.delete(vin, license);
        return Result.success();
    }
}
