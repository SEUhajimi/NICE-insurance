package com.hjb.nice.server.service;

import com.hjb.nice.entity.Home;
import java.util.List;

public interface HomeService {
    List<Home> findAll();
    Home findById(Integer homeId);
    List<Home> findByHomePolicyId(Integer hpId);
    void add(Home home);
    void update(Home home);
    void deleteById(Integer homeId);
}
