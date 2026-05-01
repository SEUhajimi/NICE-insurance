package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.AutoInvoice;
import com.hjb.nice.entity.AutoPolicy;
import com.hjb.nice.entity.Vehicle;
import com.hjb.nice.server.mapper.AutoInvoiceMapper;
import com.hjb.nice.server.mapper.AutoPaymentMapper;
import com.hjb.nice.server.mapper.AutoPolicyMapper;
import com.hjb.nice.server.mapper.DriverMapper;
import com.hjb.nice.server.mapper.VehicleMapper;
import com.hjb.nice.server.service.AutoPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AutoPolicyServiceImpl implements AutoPolicyService {

    @Autowired private AutoPolicyMapper autoPolicyMapper;
    @Autowired private VehicleMapper vehicleMapper;
    @Autowired private DriverMapper driverMapper;
    @Autowired private AutoInvoiceMapper autoInvoiceMapper;
    @Autowired private AutoPaymentMapper autoPaymentMapper;

    @Override
    public List<AutoPolicy> findAll() { return autoPolicyMapper.findAll(); }

    @Override
    public AutoPolicy findById(Integer apId) { return autoPolicyMapper.findById(apId); }

    @Override
    public List<AutoPolicy> findByCustomerId(Integer custId) { return autoPolicyMapper.findByCustomerId(custId); }

    @Override
    @Transactional
    public void add(AutoPolicy autoPolicy) {
        if (autoPolicy.getApId() != null) {
            autoPolicyMapper.insertWithId(autoPolicy);
        } else {
            autoPolicyMapper.insert(autoPolicy);
        }
    }

    @Override
    @Transactional
    public void update(AutoPolicy autoPolicy) { autoPolicyMapper.update(autoPolicy); }

    @Override
    @Transactional
    public void deleteById(Integer apId) {
        List<Vehicle> vehicles = vehicleMapper.findByAutoPolicyId(apId);
        for (Vehicle v : vehicles) {
            driverMapper.deleteByVehicleVin(v.getVin());
        }
        vehicleMapper.deleteByAutoPolicyId(apId);

        List<AutoInvoice> invoices = autoInvoiceMapper.findByAutoPolicyId(apId);
        for (AutoInvoice i : invoices) {
            autoPaymentMapper.deleteByInvoiceId(i.getIId());
        }
        autoInvoiceMapper.deleteByAutoPolicyId(apId);

        autoPolicyMapper.deleteById(apId);
    }
}
