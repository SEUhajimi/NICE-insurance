package com.hjb.nice.server.controller.portal;

import com.hjb.nice.entity.AutoPolicy;
import com.hjb.nice.entity.Home;
import com.hjb.nice.entity.HomePolicy;
import com.hjb.nice.entity.PaymentView;
import com.hjb.nice.entity.Vehicle;
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

@Tag(name = "Customer Portal", description = "Customer-facing endpoints: view own policies and invoices, make payments (requires CUSTOMER Token)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/portal")
public class CustomerPortalController {

    @Autowired
    private CustomerPortalService portalService;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Operation(summary = "View my profile")
    @GetMapping("/my-profile")
    public Result<UserProfile> myProfile() {
        return Result.success(portalService.getProfile(currentUsername()));
    }

    @Operation(summary = "Purchase insurance (creates policy and invoice, updates custType automatically)")
    @PostMapping("/purchase")
    public Result<Void> purchasePolicy(@RequestBody PurchaseRequest req) {
        portalService.purchasePolicy(currentUsername(), req);
        return Result.success();
    }

    @Operation(summary = "View my auto policies")
    @GetMapping("/my-policies/auto")
    public Result<List<AutoPolicy>> myAutoPolicies() {
        return Result.success(portalService.getAutoPolicies(currentUsername()));
    }

    @Operation(summary = "View my home policies")
    @GetMapping("/my-policies/home")
    public Result<List<HomePolicy>> myHomePolicies() {
        return Result.success(portalService.getHomePolicies(currentUsername()));
    }

    @Operation(summary = "View my vehicles (across all auto policies)")
    @GetMapping("/my-vehicles")
    public Result<List<Vehicle>> myVehicles() {
        return Result.success(portalService.getVehicles(currentUsername()));
    }

    @Operation(summary = "View my homes (across all home policies)")
    @GetMapping("/my-homes")
    public Result<List<Home>> myHomes() {
        return Result.success(portalService.getHomes(currentUsername()));
    }

    @Operation(summary = "View my invoices (including payment status)")
    @GetMapping("/my-invoices")
    public Result<List<InvoiceWithStatus>> myInvoices() {
        return Result.success(portalService.getInvoices(currentUsername()));
    }

    @Operation(summary = "View my payment history")
    @GetMapping("/my-payments")
    public Result<List<PaymentView>> myPayments() {
        return Result.success(portalService.getPayments(currentUsername()));
    }

    @Operation(summary = "Make a payment for a specified invoice (type must be AUTO or HOME)")
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
        // AUTO fields
        private String vin;
        private String mmy;
        private String vehicleStatus;
        private String driverLicense;
        private String driverFname;
        private String driverLname;
        private LocalDate driverBirthday;
        // HOME fields
        private LocalDate pdate;
        private BigDecimal pvalue;
        private Integer area;
        private String homeType;
        private Integer afn;
        private Integer hss;
        private String sp;
        private Integer basement;
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
        private List<PaymentView> payments;
    }

    @Data
    public static class PaymentRequest {
        private String type;
        private Integer invoiceId;
        private String method;
        private BigDecimal payAmount;
    }
}
