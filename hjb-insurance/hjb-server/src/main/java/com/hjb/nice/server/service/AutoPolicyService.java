package com.hjb.nice.server.service;

import com.hjb.nice.entity.AutoPolicy;
import java.util.List;

public interface AutoPolicyService {
    List<AutoPolicy> findAll();
    AutoPolicy findById(Integer apId);
    List<AutoPolicy> findByCustomerId(Integer custId);
    void add(AutoPolicy autoPolicy);
    void update(AutoPolicy autoPolicy);
    void deleteById(Integer apId);
}
