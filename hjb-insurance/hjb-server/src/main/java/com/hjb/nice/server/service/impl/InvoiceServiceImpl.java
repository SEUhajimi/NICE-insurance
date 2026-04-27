package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.*;
import com.hjb.nice.server.mapper.*;
import com.hjb.nice.server.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired private InvoiceMapper invoiceMapper;
    @Autowired private AutoInvoiceMapper autoInvoiceMapper;
    @Autowired private HomeInvoiceMapper homeInvoiceMapper;

    @Override
    public List<Invoice> findAll() { return invoiceMapper.findAll(); }

    @Override
    public Invoice findById(Integer iId) {
        AutoInvoice ai = autoInvoiceMapper.findById(iId);
        if (ai != null) return toInvoice(ai);
        HomeInvoice hi = homeInvoiceMapper.findById(iId);
        return hi != null ? toInvoice(hi) : null;
    }

    private Invoice toInvoice(AutoInvoice ai) {
        Invoice inv = new Invoice();
        inv.setIId(ai.getIId()); inv.setIDate(ai.getIDate());
        inv.setDue(ai.getDue()); inv.setAmount(ai.getAmount());
        inv.setHjbAutopolicyApId(ai.getHjbAutopolicyApId());
        return inv;
    }

    private Invoice toInvoice(HomeInvoice hi) {
        Invoice inv = new Invoice();
        inv.setIId(hi.getIId()); inv.setIDate(hi.getIDate());
        inv.setDue(hi.getDue()); inv.setAmount(hi.getAmount());
        inv.setHjbHomepolicyHpId(hi.getHjbHomepolicyHpId());
        return inv;
    }

    @Override
    @Transactional
    public void add(Invoice invoice) {
        if (invoice.getHjbAutopolicyApId() != null) {
            AutoInvoice ai = new AutoInvoice();
            ai.setIDate(invoice.getIDate());
            ai.setDue(invoice.getDue());
            ai.setAmount(invoice.getAmount());
            ai.setHjbAutopolicyApId(invoice.getHjbAutopolicyApId());
            if (invoice.getIId() != null) {
                ai.setIId(invoice.getIId());
                autoInvoiceMapper.insert(ai);
            } else {
                autoInvoiceMapper.insertAutoId(ai);
                invoice.setIId(ai.getIId());
            }
        } else {
            HomeInvoice hi = new HomeInvoice();
            hi.setIDate(invoice.getIDate());
            hi.setDue(invoice.getDue());
            hi.setAmount(invoice.getAmount());
            hi.setHjbHomepolicyHpId(invoice.getHjbHomepolicyHpId());
            if (invoice.getIId() != null) {
                hi.setIId(invoice.getIId());
                homeInvoiceMapper.insert(hi);
            } else {
                homeInvoiceMapper.insertAutoId(hi);
                invoice.setIId(hi.getIId());
            }
        }
    }

    @Override
    @Transactional
    public void update(Invoice invoice) {
        if (invoice.getHjbAutopolicyApId() != null) {
            AutoInvoice ai = new AutoInvoice();
            ai.setIId(invoice.getIId());
            ai.setIDate(invoice.getIDate());
            ai.setDue(invoice.getDue());
            ai.setAmount(invoice.getAmount());
            ai.setHjbAutopolicyApId(invoice.getHjbAutopolicyApId());
            autoInvoiceMapper.update(ai);
        } else {
            HomeInvoice hi = new HomeInvoice();
            hi.setIId(invoice.getIId());
            hi.setIDate(invoice.getIDate());
            hi.setDue(invoice.getDue());
            hi.setAmount(invoice.getAmount());
            hi.setHjbHomepolicyHpId(invoice.getHjbHomepolicyHpId());
            homeInvoiceMapper.update(hi);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer iId) {
        autoInvoiceMapper.deleteById(iId);
        homeInvoiceMapper.deleteById(iId);
    }
}
