package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.HomePolicy;
import com.hjb.nice.server.mapper.HomePolicyMapper;
import com.hjb.nice.server.service.HomePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class HomePolicyServiceImpl implements HomePolicyService {

    @Autowired
    private HomePolicyMapper homePolicyMapper;

    @Override
    public List<HomePolicy> findAll() { return homePolicyMapper.findAll(); }

    @Override
    public HomePolicy findById(Integer hpId) { return homePolicyMapper.findById(hpId); }

    @Override
    public List<HomePolicy> findByCustomerId(Integer custId) { return homePolicyMapper.findByCustomerId(custId); }

    @Override
    
    @Transactional
    public void add(HomePolicy homePolicy) { homePolicyMapper.insert(homePolicy); }

    @Override
    
    @Transactional
    public void update(HomePolicy homePolicy) { homePolicyMapper.update(homePolicy); }

    @Override
    
    @Transactional
    public void deleteById(Integer hpId) { homePolicyMapper.deleteById(hpId); }
}
