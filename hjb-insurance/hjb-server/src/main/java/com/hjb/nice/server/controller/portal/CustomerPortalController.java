package com.hjb.nice.server.controller.portal;

import com.hjb.nice.entity.AutoPolicy;
import com.hjb.nice.entity.HomePolicy;
import com.hjb.nice.entity.Payment;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.CustomerPortalService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "客户门户", description = "客户端：查看自己的保单、账单，发起支付（需 CUSTOMER Token）")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/portal")
public class CustomerPortalController {

    @Autowired
    private CustomerPortalService portalService;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Operation(summary = "查看我的资料")
    @GetMapping("/my-profile")
    public Result<UserProfile> myProfile() {
        return Result.success(portalService.getProfile(currentUsername()));
    }

    @Operation(summary = "购买保险（创建保单 + 账单，自动维护 custType）")
    @PostMapping("/purchase")
    public Result<Void> purchasePolicy(@RequestBody PurchaseRequest req) {
        portalService.purchasePolicy(currentUsername(), req);
        return Result.success();
    }

    @Operation(summary = "查看我的汽车保单")
    @GetMapping("/my-policies/auto")
    public Result<List<AutoPolicy>> myAutoPolicies() {
        return Result.success(portalService.getAutoPolicies(currentUsername()));
    }

    @Operation(summary = "查看我的房屋保单")
    @GetMapping("/my-policies/home")
    public Result<List<HomePolicy>> myHomePolicies() {
        return Result.success(portalService.getHomePolicies(currentUsername()));
    }

    @Operation(summary = "查看我的账单（含支付状态）")
    @GetMapping("/my-invoices")
    public Result<List<InvoiceWithStatus>> myInvoices() {
        return Result.success(portalService.getInvoices(currentUsername()));
    }

    @Operation(summary = "查看我的支付历史")
    @GetMapping("/my-payments")
    public Result<List<Payment>> myPayments() {
        return Result.success(portalService.getPayments(currentUsername()));
    }

    @Operation(summary = "为指定 Invoice 发起支付")
    @PostMapping("/payments")
    public Result<Void> makePayment(@RequestBody PaymentRequest req) {
        portalService.makePayment(currentUsername(), req);
        return Result.success();
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
        private String custType;
    }

    @Data
    public static class PurchaseRequest {
        private String type;
        private BigDecimal amount;
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
