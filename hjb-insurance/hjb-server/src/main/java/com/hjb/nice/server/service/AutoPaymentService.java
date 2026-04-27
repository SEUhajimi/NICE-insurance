package com.hjb.nice.server.service;

import com.hjb.nice.entity.AutoPayment;
import java.util.List;

public interface AutoPaymentService {
    List<AutoPayment> findAll();
    AutoPayment findById(Integer pId);
    void add(AutoPayment payment);
    void update(AutoPayment payment);
    void deleteById(Integer pId);
}
