package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.*;
import com.hjb.nice.enums.CustomerType;
import com.hjb.nice.enums.PolicyStatus;
import com.hjb.nice.server.controller.portal.CustomerPortalController.*;
import com.hjb.nice.server.exception.NotFoundException;
import com.hjb.nice.server.exception.UnauthorizedException;
import com.hjb.nice.server.exception.ValidationException;
import com.hjb.nice.server.mapper.*;
import com.hjb.nice.server.service.CustomerPortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerPortalServiceImpl implements CustomerPortalService {

    @Autowired private CustomerAccountMapper customerAccountMapper;
    @Autowired private CustomerMapper customerMapper;
    @Autowired private AutoPolicyMapper autoPolicyMapper;
    @Autowired private HomePolicyMapper homePolicyMapper;
    @Autowired private InvoiceMapper invoiceMapper;
    @Autowired private PaymentMapper paymentMapper;

    private CustomerAccount getAccount(String username) {
        CustomerAccount account = customerAccountMapper.findByUsername(username);
        if (account == null) throw new NotFoundException("账号信息不存在");
        return account;
    }

    @Override
    public UserProfile getProfile(String username) {
        CustomerAccount account = getAccount(username);
        UserProfile profile = new UserProfile();
        profile.setUsername(account.getUsername());
        profile.setEmail(account.getEmail());
        profile.setFname(account.getFname());
        profile.setLname(account.getLname());
        profile.setGender(account.getGender());
        profile.setMaritalStatus(account.getMaritalStatus());
        profile.setAddrStreet(account.getAddrStreet());
        profile.setAddrCity(account.getAddrCity());
        profile.setAddrState(account.getAddrState());
        profile.setZipcode(account.getZipcode());
        if (account.getCustomerId() != null) {
            Customer customer = customerMapper.findById(account.getCustomerId());
            if (customer != null) profile.setCustType(customer.getCustType());
        }
        return profile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void purchasePolicy(String username, PurchaseRequest req) {
        if (!"AUTO".equals(req.getType()) && !"HOME".equals(req.getType())) {
            throw new ValidationException("保险类型无效，必须为 AUTO 或 HOME");
        }
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("保险金额必须大于 0");
        }

        CustomerAccount account = getAccount(username);

        // 首次购险：直接使用已读取的 account 数据，不做二次查询
        if (account.getCustomerId() == null) {
            Customer customer = new Customer();
            customer.setFname(account.getFname());
            customer.setLname(account.getLname());
            customer.setGender(account.getGender());
            customer.setMaritalStatus(account.getMaritalStatus());
            customer.setCustType(null);
            customer.setAddrStreet(account.getAddrStreet());
            customer.setAddrCity(account.getAddrCity());
            customer.setAddrState(account.getAddrState());
            customer.setZipcode(account.getZipcode());
            customerMapper.insertForRegister(customer);
            customerAccountMapper.updateCustomerId(account.getAccountId(), customer.getCustId());
            account.setCustomerId(customer.getCustId()); // 内存更新，无需重查
        }

        Integer custId = account.getCustomerId();
        LocalDate today = LocalDate.now();

        Invoice invoice = new Invoice();
        invoice.setIDate(today);
        invoice.setDue(today.plusDays(30));
        invoice.setAmount(req.getAmount());

        if ("AUTO".equals(req.getType())) {
            AutoPolicy policy = new AutoPolicy();
            policy.setSdate(today);
            policy.setEdate(today.plusYears(1));
            policy.setAmount(req.getAmount());
            policy.setStatus(PolicyStatus.CURRENT.getCode());
            policy.setHjbCustomerCustId(custId);
            autoPolicyMapper.insertAutoId(policy);
            invoice.setHjbAutopolicyApId(policy.getApId());
        } else {
            HomePolicy policy = new HomePolicy();
            policy.setSdate(today);
            policy.setEdate(today.plusYears(1));
            policy.setAmount(req.getAmount());
            policy.setStatus(PolicyStatus.CURRENT.getCode());
            policy.setHjbCustomerCustId(custId);
            homePolicyMapper.insertAutoId(policy);
            invoice.setHjbHomepolicyHpId(policy.getHpId());
        }
        invoiceMapper.insertAutoId(invoice);

        boolean hasAuto = !autoPolicyMapper.findByCustomerId(custId).isEmpty();
        boolean hasHome = !homePolicyMapper.findByCustomerId(custId).isEmpty();
        customerMapper.updateCustType(custId, CustomerType.calculate(hasAuto, hasHome));
    }

    @Override
    public List<AutoPolicy> getAutoPolicies(String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return List.of();
        return autoPolicyMapper.findByCustomerId(account.getCustomerId());
    }

    @Override
    public List<HomePolicy> getHomePolicies(String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return List.of();
        return homePolicyMapper.findByCustomerId(account.getCustomerId());
    }

    @Override
    public List<InvoiceWithStatus> getInvoices(String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return List.of();
        Integer custId = account.getCustomerId();
        List<InvoiceWithStatus> result = new ArrayList<>();

        autoPolicyMapper.findByCustomerId(custId).forEach(ap ->
            invoiceMapper.findByAutoPolicyId(ap.getApId()).forEach(inv ->
                result.add(buildInvoiceStatus(inv, "Auto"))));

        homePolicyMapper.findByCustomerId(custId).forEach(hp ->
            invoiceMapper.findByHomePolicyId(hp.getHpId()).forEach(inv ->
                result.add(buildInvoiceStatus(inv, "Home"))));

        return result;
    }

    @Override
    public List<Payment> getPayments(String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return List.of();
        Integer custId = account.getCustomerId();
        List<Payment> payments = new ArrayList<>();

        autoPolicyMapper.findByCustomerId(custId).forEach(ap ->
            invoiceMapper.findByAutoPolicyId(ap.getApId()).forEach(inv ->
                payments.addAll(paymentMapper.findByInvoiceId(inv.getIId()))));

        homePolicyMapper.findByCustomerId(custId).forEach(hp ->
            invoiceMapper.findByHomePolicyId(hp.getHpId()).forEach(inv ->
                payments.addAll(paymentMapper.findByInvoiceId(inv.getIId()))));

        return payments;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void makePayment(String username, PaymentRequest req) {
        if (req.getPayAmount() == null || req.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("支付金额必须大于 0");
        }
        if (req.getPayAmount().scale() > 2) {
            throw new ValidationException("支付金额最多保留两位小数");
        }

        Invoice invoice = invoiceMapper.findById(req.getInvoiceId());
        if (invoice == null) throw new NotFoundException("Invoice 不存在");

        // 用单条 SQL 校验归属，避免 N+1 查询
        if (!invoiceBelongsToUser(invoice, username)) {
            throw new UnauthorizedException("无权操作该账单");
        }

        BigDecimal paid = paymentMapper.findByInvoiceId(req.getInvoiceId()).stream()
                .map(Payment::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (paid.compareTo(invoice.getAmount()) >= 0) {
            throw new ValidationException("该账单已全额支付");
        }

        BigDecimal remaining = invoice.getAmount().subtract(paid);
        if (req.getPayAmount().compareTo(remaining) > 0) {
            throw new ValidationException("支付金额不能超过剩余应付金额 " + remaining);
        }

        Payment payment = new Payment();
        payment.setHjbInvoiceIId(req.getInvoiceId());
        payment.setMethod(req.getMethod());
        payment.setPayAmount(req.getPayAmount());
        payment.setPayDate(LocalDate.now());
        paymentMapper.insertAutoId(payment);
    }

    /** 用精准 SQL 查询替代全量遍历，O(1) 而非 O(n) */
    private boolean invoiceBelongsToUser(Invoice invoice, String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return false;
        Integer custId = account.getCustomerId();

        if (invoice.getHjbAutopolicyApId() != null) {
            return autoPolicyMapper.findByIdAndCustomerId(invoice.getHjbAutopolicyApId(), custId) != null;
        }
        if (invoice.getHjbHomepolicyHpId() != null) {
            return homePolicyMapper.findByIdAndCustomerId(invoice.getHjbHomepolicyHpId(), custId) != null;
        }
        return false;
    }

    private InvoiceWithStatus buildInvoiceStatus(Invoice inv, String policyType) {
        List<Payment> payments = paymentMapper.findByInvoiceId(inv.getIId());
        BigDecimal paidAmount = payments.stream()
                .map(Payment::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        InvoiceWithStatus dto = new InvoiceWithStatus();
        dto.setIId(inv.getIId());
        dto.setIDate(inv.getIDate());
        dto.setDue(inv.getDue());
        dto.setAmount(inv.getAmount());
        dto.setHjbAutopolicyApId(inv.getHjbAutopolicyApId());
        dto.setHjbHomepolicyHpId(inv.getHjbHomepolicyHpId());
        dto.setPolicyType(policyType);
        dto.setPaidAmount(paidAmount);
        dto.setPaid(paidAmount.compareTo(inv.getAmount()) >= 0);
        dto.setPayments(payments);
        return dto;
    }
}
