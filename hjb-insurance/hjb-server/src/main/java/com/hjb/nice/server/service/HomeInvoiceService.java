package com.hjb.nice.server.service;

import com.hjb.nice.entity.HomeInvoice;
import java.util.List;

public interface HomeInvoiceService {
    List<HomeInvoice> findAll();
    HomeInvoice findById(Integer iId);
    void add(HomeInvoice invoice);
    void update(HomeInvoice invoice);
    void deleteById(Integer iId);
}
