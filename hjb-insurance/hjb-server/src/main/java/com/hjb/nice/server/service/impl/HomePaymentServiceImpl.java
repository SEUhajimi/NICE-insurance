package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.HomePayment;
import com.hjb.nice.server.mapper.HomePaymentMapper;
import com.hjb.nice.server.service.HomePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class HomePaymentServiceImpl implements HomePaymentService {

    @Autowired
    private HomePaymentMapper homePaymentMapper;

    @Override
    public List<HomePayment> findAll() { return homePaymentMapper.findAll(); }

    @Override
    public HomePayment findById(Integer pId) { return homePaymentMapper.findById(pId); }

    @Override
    @Transactional
    public void add(HomePayment payment) { homePaymentMapper.insert(payment); }

    @Override
    @Transactional
    public void update(HomePayment payment) { homePaymentMapper.update(payment); }

    @Override
    @Transactional
    public void deleteById(Integer pId) { homePaymentMapper.deleteById(pId); }
}
