package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.Payment;
import com.hjb.nice.server.mapper.PaymentMapper;
import com.hjb.nice.server.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public List<Payment> findAll() { return paymentMapper.findAll(); }

    @Override
    public Payment findById(Integer pId) { return paymentMapper.findById(pId); }

    @Override
    public void add(Payment payment) { paymentMapper.insert(payment); }

    @Override
    public void update(Payment payment) { paymentMapper.update(payment); }

    @Override
    public void deleteById(Integer pId) { paymentMapper.deleteById(pId); }
}
