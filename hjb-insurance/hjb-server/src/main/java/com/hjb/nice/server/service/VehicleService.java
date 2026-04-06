package com.hjb.nice.server.service;

import com.hjb.nice.entity.Vehicle;
import java.util.List;

public interface VehicleService {
    List<Vehicle> findAll();
    Vehicle findById(String vin);
    List<Vehicle> findByAutoPolicyId(Integer apId);
    void add(Vehicle vehicle);
    void update(Vehicle vehicle);
    void deleteById(String vin);
}
