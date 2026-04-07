package com.hjb.nice.server.controller.portal;

import com.hjb.nice.entity.*;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "客户门户", description = "客户端：查看自己的保单、账单，发起支付（需 CUSTOMER Token）")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/portal")
public class CustomerPortalController {

    @Autowired private CustomerAccountMapper customerAccountMapper;
    @Autowired private CustomerMapper customerMapper;
    @Autowired private AutoPolicyMapper autoPolicyMapper;
    @Autowired private HomePolicyMapper homePolicyMapper;
    @Autowired private InvoiceMapper invoiceMapper;
    @Autowired private PaymentMapper paymentMapper;

    private CustomerAccount currentAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerAccount account = customerAccountMapper.findByUsername(username);
        if (account == null) throw new RuntimeException("账号信息不存在");
        return account;
    }

    /** 返回当前用户的 customerId，未下单则返回 null */
    private Integer currentCustomerId() {
        return currentAccount().getCustomerId();
    }

    @Operation(summary = "查看我的资料")
    @GetMapping("/my-profile")
    public Result<UserProfile> myProfile() {
        CustomerAccount account = currentAccount();
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
        return Result.success(profile);
    }

    @Operation(summary = "购买保险（创建保单 + 账单，自动维护 custType）")
    @PostMapping("/purchase")
    @Transactional
    public Result<Void> purchasePolicy(@RequestBody PurchaseRequest req) {
        CustomerAccount account = currentAccount();

        // 1. 首次购买：先创建 hjb_customer 记录
        if (account.getCustomerId() == null) {
            Customer customer = new Customer();
            customer.setFname(account.getFname());
            customer.setLname(account.getLname());
            customer.setGender(account.getGender());
            customer.setMaritalStatus(account.getMaritalStatus());
            customer.setCustType(null);  // 下面会自动计算
            customer.setAddrStreet(account.getAddrStreet());
            customer.setAddrCity(account.getAddrCity());
            customer.setAddrState(account.getAddrState());
            customer.setZipcode(account.getZipcode());
            customerMapper.insertForRegister(customer);
            customerAccountMapper.updateCustomerId(account.getAccountId(), customer.getCustId());
            // 刷新 account 使后续逻辑能取到 customerId
            account = currentAccount();
        }

        Integer custId = account.getCustomerId();
        LocalDate today = LocalDate.now();
        LocalDate sdate = today;
        LocalDate edate = today.plusYears(1);
        String status = "C";

        // 2. 创建保单 + 账单
        Invoice invoice = new Invoice();
        invoice.setIDate(today);
        invoice.setDue(today.plusDays(30));
        invoice.setAmount(req.getAmount());

        if ("AUTO".equals(req.getType())) {
            AutoPolicy policy = new AutoPolicy();
            policy.setSdate(sdate);
            policy.setEdate(edate);
            policy.setAmount(req.getAmount());
            policy.setStatus(status);
            policy.setHjbCustomerCustId(custId);
            autoPolicyMapper.insertAutoId(policy);
            invoice.setHjbAutopolicyApId(policy.getApId());
        } else {
            HomePolicy policy = new HomePolicy();
            policy.setSdate(sdate);
            policy.setEdate(edate);
            policy.setAmount(req.getAmount());
            policy.setStatus(status);
            policy.setHjbCustomerCustId(custId);
            homePolicyMapper.insertAutoId(policy);
            invoice.setHjbHomepolicyHpId(policy.getHpId());
        }
        invoiceMapper.insertAutoId(invoice);

        // 3. 根据实际保单自动更新 custType
        boolean hasAuto = !autoPolicyMapper.findByCustomerId(custId).isEmpty();
        boolean hasHome = !homePolicyMapper.findByCustomerId(custId).isEmpty();
        String custType = (hasAuto && hasHome) ? "B" : hasAuto ? "A" : "H";
        customerMapper.updateCustType(custId, custType);

        return Result.success();
    }

    @Operation(summary = "查看我的汽车保单")
    @GetMapping("/my-policies/auto")
    public Result<List<AutoPolicy>> myAutoPolicies() {
        Integer custId = currentCustomerId();
        if (custId == null) return Result.success(List.of());
        return Result.success(autoPolicyMapper.findByCustomerId(custId));
    }

    @Operation(summary = "查看我的房屋保单")
    @GetMapping("/my-policies/home")
    public Result<List<HomePolicy>> myHomePolicies() {
        Integer custId = currentCustomerId();
        if (custId == null) return Result.success(List.of());
        return Result.success(homePolicyMapper.findByCustomerId(custId));
    }

    @Operation(summary = "查看我的账单（含支付状态）")
    @GetMapping("/my-invoices")
    public Result<List<InvoiceWithStatus>> myInvoices() {
        Integer custId = currentCustomerId();
        if (custId == null) return Result.success(List.of());
        List<InvoiceWithStatus> result = new ArrayList<>();

        autoPolicyMapper.findByCustomerId(custId).forEach(ap ->
            invoiceMapper.findByAutoPolicyId(ap.getApId()).forEach(inv ->
                result.add(buildInvoiceStatus(inv, "Auto"))));

        homePolicyMapper.findByCustomerId(custId).forEach(hp ->
            invoiceMapper.findByHomePolicyId(hp.getHpId()).forEach(inv ->
                result.add(buildInvoiceStatus(inv, "Home"))));

        return Result.success(result);
    }

    @Operation(summary = "查看我的支付历史")
    @GetMapping("/my-payments")
    public Result<List<Payment>> myPayments() {
        Integer custId = currentCustomerId();
        if (custId == null) return Result.success(List.of());
        List<Payment> payments = new ArrayList<>();

        autoPolicyMapper.findByCustomerId(custId).forEach(ap ->
            invoiceMapper.findByAutoPolicyId(ap.getApId()).forEach(inv ->
                payments.addAll(paymentMapper.findByInvoiceId(inv.getIId()))));

        homePolicyMapper.findByCustomerId(custId).forEach(hp ->
            invoiceMapper.findByHomePolicyId(hp.getHpId()).forEach(inv ->
                payments.addAll(paymentMapper.findByInvoiceId(inv.getIId()))));

        return Result.success(payments);
    }

    @Operation(summary = "为指定 Invoice 发起支付")
    @PostMapping("/payments")
    public Result<Void> makePayment(@RequestBody PaymentRequest req) {
        Invoice invoice = invoiceMapper.findById(req.getInvoiceId());
        if (invoice == null) throw new RuntimeException("Invoice 不存在");

        BigDecimal paid = paymentMapper.findByInvoiceId(req.getInvoiceId()).stream()
                .map(Payment::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (paid.compareTo(invoice.getAmount()) >= 0) {
            throw new RuntimeException("该账单已全额支付");
        }

        Payment payment = new Payment();
        payment.setHjbInvoiceIId(req.getInvoiceId());
        payment.setMethod(req.getMethod());
        payment.setPayAmount(req.getPayAmount());
        payment.setPayDate(LocalDate.now());
        paymentMapper.insertAutoId(payment);
        return Result.success();
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

    // ── DTOs ──────────────────────────────────────────────────────
    @Data
    public static class UserProfile {
        private String username;
        private String email;
        private String fname;
        private String lname;
        private String gender;
        private String maritalStatus;
        private String addrStreet;
        private String addrCity;
        private String addrState;
        private String zipcode;
        private String custType;   // null = 尚未申请保险
    }

    @Data
    public static class PurchaseRequest {
        private String type;          // "AUTO" or "HOME"
        private BigDecimal amount;    // 年保费，由套餐决定
    }

    @Data
    public static class InvoiceWithStatus {
        @JsonProperty("iId")
        private Integer iId;
        @JsonProperty("iDate")
        private LocalDate iDate;
        private LocalDate due;
        private BigDecimal amount;
        private Integer hjbAutopolicyApId;
        private Integer hjbHomepolicyHpId;
        private String policyType;
        private BigDecimal paidAmount;
        private boolean paid;
        private List<Payment> payments;
    }

    @Data
    public static class PaymentRequest {
        private Integer invoiceId;
        private String method;
        private BigDecimal payAmount;
    }
}
