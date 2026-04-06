package com.hjb.nice.server.service;

import com.hjb.nice.entity.Driver;
import java.util.List;

public interface DriverService {
    List<Driver> findAll();
    Driver findById(String driverLicense);
    void add(Driver driver);
    void update(Driver driver);
    void deleteById(String driverLicense);
}
