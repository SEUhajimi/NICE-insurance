package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.*;
import com.hjb.nice.server.mapper.*;
import com.hjb.nice.server.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired private PaymentMapper paymentMapper;
    @Autowired private AutoPaymentMapper autoPaymentMapper;
    @Autowired private HomePaymentMapper homePaymentMapper;
    @Autowired private AutoInvoiceMapper autoInvoiceMapper;

    @Override
    public List<Payment> findAll() { return paymentMapper.findAll(); }

    @Override
    public Payment findById(Integer pId) {
        AutoPayment ap = autoPaymentMapper.findById(pId);
        if (ap != null) return toPayment(ap);
        HomePayment hp = homePaymentMapper.findById(pId);
        return hp != null ? toPayment(hp) : null;
    }

    private Payment toPayment(AutoPayment ap) {
        Payment p = new Payment();
        p.setPId(ap.getPId()); p.setMethod(ap.getMethod());
        p.setHjbInvoiceIId(ap.getHjbAutoInvoiceIId());
        p.setPayAmount(ap.getPayAmount()); p.setPayDate(ap.getPayDate());
        return p;
    }

    private Payment toPayment(HomePayment hp) {
        Payment p = new Payment();
        p.setPId(hp.getPId()); p.setMethod(hp.getMethod());
        p.setHjbInvoiceIId(hp.getHjbHomeInvoiceIId());
        p.setPayAmount(hp.getPayAmount()); p.setPayDate(hp.getPayDate());
        return p;
    }

    @Override
    @Transactional
    public void add(Payment payment) {
        // Determine which invoice table owns this invoice ID
        if (autoInvoiceMapper.findById(payment.getHjbInvoiceIId()) != null) {
            AutoPayment ap = new AutoPayment();
            ap.setMethod(payment.getMethod());
            ap.setHjbAutoInvoiceIId(payment.getHjbInvoiceIId());
            ap.setPayAmount(payment.getPayAmount());
            ap.setPayDate(payment.getPayDate());
            autoPaymentMapper.insertAutoId(ap);
            payment.setPId(ap.getPId());
        } else {
            HomePayment hp = new HomePayment();
            hp.setMethod(payment.getMethod());
            hp.setHjbHomeInvoiceIId(payment.getHjbInvoiceIId());
            hp.setPayAmount(payment.getPayAmount());
            hp.setPayDate(payment.getPayDate());
            homePaymentMapper.insertAutoId(hp);
            payment.setPId(hp.getPId());
        }
    }

    @Override
    @Transactional
    public void update(Payment payment) {
        if (autoInvoiceMapper.findById(payment.getHjbInvoiceIId()) != null) {
            AutoPayment ap = new AutoPayment();
            ap.setPId(payment.getPId());
            ap.setMethod(payment.getMethod());
            ap.setHjbAutoInvoiceIId(payment.getHjbInvoiceIId());
            ap.setPayAmount(payment.getPayAmount());
            ap.setPayDate(payment.getPayDate());
            autoPaymentMapper.update(ap);
        } else {
            HomePayment hp = new HomePayment();
            hp.setPId(payment.getPId());
            hp.setMethod(payment.getMethod());
            hp.setHjbHomeInvoiceIId(payment.getHjbInvoiceIId());
            hp.setPayAmount(payment.getPayAmount());
            hp.setPayDate(payment.getPayDate());
            homePaymentMapper.update(hp);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer pId) {
        autoPaymentMapper.deleteById(pId);
        homePaymentMapper.deleteById(pId);
    }
}
