package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.Invoice;
import com.hjb.nice.server.mapper.InvoiceMapper;
import com.hjb.nice.server.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Override
    public List<Invoice> findAll() { return invoiceMapper.findAll(); }

    @Override
    public Invoice findById(Integer iId) { return invoiceMapper.findById(iId); }

    @Override
    
    @Transactional
    public void add(Invoice invoice) { invoiceMapper.insert(invoice); }

    @Override
    
    @Transactional
    public void update(Invoice invoice) { invoiceMapper.update(invoice); }

    @Override
    
    @Transactional
    public void deleteById(Integer iId) { invoiceMapper.deleteById(iId); }
}
