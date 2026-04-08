package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.Home;
import com.hjb.nice.server.mapper.HomeMapper;
import com.hjb.nice.server.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private HomeMapper homeMapper;

    @Override
    public List<Home> findAll() { return homeMapper.findAll(); }

    @Override
    public Home findById(Integer homeId) { return homeMapper.findById(homeId); }

    @Override
    public List<Home> findByHomePolicyId(Integer hpId) { return homeMapper.findByHomePolicyId(hpId); }

    @Override
    
    @Transactional
    public void add(Home home) { homeMapper.insert(home); }

    @Override
    
    @Transactional
    public void update(Home home) { homeMapper.update(home); }

    @Override
    
    @Transactional
    public void deleteById(Integer homeId) { homeMapper.deleteById(homeId); }
}
