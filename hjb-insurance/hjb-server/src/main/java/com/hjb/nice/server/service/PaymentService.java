package com.hjb.nice.server.service;

import com.hjb.nice.entity.Payment;
import java.util.List;

public interface PaymentService {
    List<Payment> findAll();
    Payment findById(Integer pId);
    void add(Payment payment);
    void update(Payment payment);
    void deleteById(Integer pId);
}
