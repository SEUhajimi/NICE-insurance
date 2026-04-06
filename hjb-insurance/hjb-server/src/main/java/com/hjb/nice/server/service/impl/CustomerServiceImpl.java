package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.Customer;
import com.hjb.nice.server.mapper.CustomerMapper;
import com.hjb.nice.server.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public List<Customer> findAll() {
        return customerMapper.findAll();
    }

    @Override
    public Customer findById(Integer custId) {
        return customerMapper.findById(custId);
    }

    @Override
    public void add(Customer customer) {
        customerMapper.insert(customer);
    }

    @Override
    public void update(Customer customer) {
        customerMapper.update(customer);
    }

    @Override
    public void deleteById(Integer custId) {
        customerMapper.deleteById(custId);
    }
}
