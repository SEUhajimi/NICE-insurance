package com.hjb.nice.server.service;

import com.hjb.nice.entity.Customer;
import java.util.List;

public interface CustomerService {
    List<Customer> findAll();
    Customer findById(Integer custId);
    void add(Customer customer);
    void update(Customer customer);
    void deleteById(Integer custId);
}
