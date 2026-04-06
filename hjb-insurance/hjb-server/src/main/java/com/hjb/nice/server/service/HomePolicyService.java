package com.hjb.nice.server.service;

import com.hjb.nice.entity.HomePolicy;
import java.util.List;

public interface HomePolicyService {
    List<HomePolicy> findAll();
    HomePolicy findById(Integer hpId);
    List<HomePolicy> findByCustomerId(Integer custId);
    void add(HomePolicy homePolicy);
    void update(HomePolicy homePolicy);
    void deleteById(Integer hpId);
}
