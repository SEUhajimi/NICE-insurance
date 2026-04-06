package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.Vehicle;
import com.hjb.nice.server.mapper.VehicleMapper;
import com.hjb.nice.server.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public List<Vehicle> findAll() { return vehicleMapper.findAll(); }

    @Override
    public Vehicle findById(String vin) { return vehicleMapper.findById(vin); }

    @Override
    public List<Vehicle> findByAutoPolicyId(Integer apId) { return vehicleMapper.findByAutoPolicyId(apId); }

    @Override
    public void add(Vehicle vehicle) { vehicleMapper.insert(vehicle); }

    @Override
    public void update(Vehicle vehicle) { vehicleMapper.update(vehicle); }

    @Override
    public void deleteById(String vin) { vehicleMapper.deleteById(vin); }
}
