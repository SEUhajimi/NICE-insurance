package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.HomeInvoice;
import com.hjb.nice.server.mapper.HomeInvoiceMapper;
import com.hjb.nice.server.service.HomeInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class HomeInvoiceServiceImpl implements HomeInvoiceService {

    @Autowired
    private HomeInvoiceMapper homeInvoiceMapper;

    @Override
    public List<HomeInvoice> findAll() { return homeInvoiceMapper.findAll(); }

    @Override
    public HomeInvoice findById(Integer iId) { return homeInvoiceMapper.findById(iId); }

    @Override
    @Transactional
    public void add(HomeInvoice invoice) { homeInvoiceMapper.insert(invoice); }

    @Override
    @Transactional
    public void update(HomeInvoice invoice) { homeInvoiceMapper.update(invoice); }

    @Override
    @Transactional
    public void deleteById(Integer iId) { homeInvoiceMapper.deleteById(iId); }
}
