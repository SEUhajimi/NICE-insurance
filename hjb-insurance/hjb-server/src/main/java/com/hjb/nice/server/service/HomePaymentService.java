package com.hjb.nice.server.service;

import com.hjb.nice.entity.HomePayment;
import java.util.List;

public interface HomePaymentService {
    List<HomePayment> findAll();
    HomePayment findById(Integer pId);
    void add(HomePayment payment);
    void update(HomePayment payment);
    void deleteById(Integer pId);
}
