package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.AutoPolicy;
import com.hjb.nice.server.mapper.AutoPolicyMapper;
import com.hjb.nice.server.service.AutoPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AutoPolicyServiceImpl implements AutoPolicyService {

    @Autowired
    private AutoPolicyMapper autoPolicyMapper;

    @Override
    public List<AutoPolicy> findAll() { return autoPolicyMapper.findAll(); }

    @Override
    public AutoPolicy findById(Integer apId) { return autoPolicyMapper.findById(apId); }

    @Override
    public List<AutoPolicy> findByCustomerId(Integer custId) { return autoPolicyMapper.findByCustomerId(custId); }

    @Override
    public void add(AutoPolicy autoPolicy) { autoPolicyMapper.insert(autoPolicy); }

    @Override
    public void update(AutoPolicy autoPolicy) { autoPolicyMapper.update(autoPolicy); }

    @Override
    public void deleteById(Integer apId) { autoPolicyMapper.deleteById(apId); }
}
