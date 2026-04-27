package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.AutoInvoice;
import com.hjb.nice.server.mapper.AutoInvoiceMapper;
import com.hjb.nice.server.service.AutoInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AutoInvoiceServiceImpl implements AutoInvoiceService {

    @Autowired
    private AutoInvoiceMapper autoInvoiceMapper;

    @Override
    public List<AutoInvoice> findAll() { return autoInvoiceMapper.findAll(); }

    @Override
    public AutoInvoice findById(Integer iId) { return autoInvoiceMapper.findById(iId); }

    @Override
    @Transactional
    public void add(AutoInvoice invoice) { autoInvoiceMapper.insert(invoice); }

    @Override
    @Transactional
    public void update(AutoInvoice invoice) { autoInvoiceMapper.update(invoice); }

    @Override
    @Transactional
    public void deleteById(Integer iId) { autoInvoiceMapper.deleteById(iId); }
}
