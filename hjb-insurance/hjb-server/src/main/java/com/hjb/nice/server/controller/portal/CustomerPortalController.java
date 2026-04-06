package com.hjb.nice.server.controller.portal;

import com.hjb.nice.entity.*;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "客户门户", description = "客户端：查看自己的保单、账单，发起支付（需 CUSTOMER Token）")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/portal")
public class CustomerPortalController {

    @Autowired
    private CustomerAccountMapper customerAccountMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private AutoPolicyMapper autoPolicyMapper;

    @Autowired
    private HomePolicyMapper homePolicyMapper;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    // ── 获取当前登录客户的 customerId ──────────────────────────────
    private Integer currentCustomerId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerAccount account = customerAccountMapper.findByUsername(username);
        if (account == null) throw new RuntimeException("账号信息不存在");
        return account.getCustomerId();
    }

    // ── 个人资料 ──────────────────────────────────────────────────
    @Operation(summary = "查看我的资料")
    @GetMapping("/my-profile")
    public Result<Customer> myProfile() {
        return Result.success(customerMapper.findById(currentCustomerId()));
    }

    // ── 保单 ──────────────────────────────────────────────────────
    @Operation(summary = "查看我的汽车保单")
    @GetMapping("/my-policies/auto")
    public Result<List<AutoPolicy>> myAutoPolicies() {
        return Result.success(autoPolicyMapper.findByCustomerId(currentCustomerId()));
    }

    @Operation(summary = "查看我的房屋保单")
    @GetMapping("/my-policies/home")
    public Result<List<HomePolicy>> myHomePolicies() {
        return Result.success(homePolicyMapper.findByCustomerId(currentCustomerId()));
    }

    // ── Invoice ───────────────────────────────────────────────────
    @Operation(summary = "查看我的所有账单",
               description = "返回当前客户名下所有保单对应的全部 Invoice")
    @GetMapping("/my-invoices")
    public Result<List<Invoice>> myInvoices() {
        Integer custId = currentCustomerId();
        List<Invoice> invoices = new ArrayList<>();

        autoPolicyMapper.findByCustomerId(custId)
                .forEach(ap -> invoices.addAll(invoiceMapper.findByAutoPolicyId(ap.getApId())));

        homePolicyMapper.findByCustomerId(custId)
                .forEach(hp -> invoices.addAll(invoiceMapper.findByHomePolicyId(hp.getHpId())));

        return Result.success(invoices);
    }

    // ── Payment ───────────────────────────────────────────────────
    @Operation(summary = "为指定 Invoice 发起支付")
    @PostMapping("/payments")
    public Result<Void> makePayment(@RequestBody PaymentRequest req) {
        // 验证 invoice 存在
        Invoice invoice = invoiceMapper.findById(req.getInvoiceId());
        if (invoice == null) throw new RuntimeException("Invoice 不存在");

        Payment payment = new Payment();
        payment.setHjbInvoiceIId(req.getInvoiceId());
        payment.setMethod(req.getMethod());
        payment.setPayAmount(req.getPayAmount());
        payment.setPayDate(LocalDate.now());
        paymentMapper.insertAutoId(payment);

        return Result.success();
    }

    // ── 内部 DTO ──────────────────────────────────────────────────
    @lombok.Data
    public static class PaymentRequest {
        private Integer invoiceId;
        private String method;          // e.g. "Credit Card", "Bank Transfer"
        private java.math.BigDecimal payAmount;
    }
}
