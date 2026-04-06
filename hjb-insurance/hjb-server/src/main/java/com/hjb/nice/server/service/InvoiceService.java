package com.hjb.nice.server.service;

import com.hjb.nice.entity.Invoice;
import java.util.List;

public interface InvoiceService {
    List<Invoice> findAll();
    Invoice findById(Integer iId);
    void add(Invoice invoice);
    void update(Invoice invoice);
    void deleteById(Integer iId);
}
