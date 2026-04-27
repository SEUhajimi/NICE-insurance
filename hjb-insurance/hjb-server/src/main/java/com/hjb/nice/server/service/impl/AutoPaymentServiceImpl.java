package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.AutoPayment;
import com.hjb.nice.server.mapper.AutoPaymentMapper;
import com.hjb.nice.server.service.AutoPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AutoPaymentServiceImpl implements AutoPaymentService {

    @Autowired
    private AutoPaymentMapper autoPaymentMapper;

    @Override
    public List<AutoPayment> findAll() { return autoPaymentMapper.findAll(); }

    @Override
    public AutoPayment findById(Integer pId) { return autoPaymentMapper.findById(pId); }

    @Override
    @Transactional
    public void add(AutoPayment payment) { autoPaymentMapper.insert(payment); }

    @Override
    @Transactional
    public void update(AutoPayment payment) { autoPaymentMapper.update(payment); }

    @Override
    @Transactional
    public void deleteById(Integer pId) { autoPaymentMapper.deleteById(pId); }
}
