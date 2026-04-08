package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.Driver;
import com.hjb.nice.server.mapper.DriverMapper;
import com.hjb.nice.server.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverMapper driverMapper;

    @Override
    public List<Driver> findAll() { return driverMapper.findAll(); }

    @Override
    public Driver findById(String driverLicense) { return driverMapper.findById(driverLicense); }

    @Override
    
    @Transactional
    public void add(Driver driver) { driverMapper.insert(driver); }

    @Override
    
    @Transactional
    public void update(Driver driver) { driverMapper.update(driver); }

    @Override
    
    @Transactional
    public void deleteById(String driverLicense) { driverMapper.deleteById(driverLicense); }
}
