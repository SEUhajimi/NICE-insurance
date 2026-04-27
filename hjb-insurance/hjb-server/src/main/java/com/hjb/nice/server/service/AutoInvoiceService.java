package com.hjb.nice.server.service;

import com.hjb.nice.entity.AutoInvoice;
import java.util.List;

public interface AutoInvoiceService {
    List<AutoInvoice> findAll();
    AutoInvoice findById(Integer iId);
    void add(AutoInvoice invoice);
    void update(AutoInvoice invoice);
    void deleteById(Integer iId);
}
