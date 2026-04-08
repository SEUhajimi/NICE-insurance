package com.hjb.nice.server.service;

import com.hjb.nice.entity.AutoPolicy;
import com.hjb.nice.entity.HomePolicy;
import com.hjb.nice.entity.Payment;
import com.hjb.nice.server.controller.portal.CustomerPortalController.InvoiceWithStatus;
import com.hjb.nice.server.controller.portal.CustomerPortalController.PaymentRequest;
import com.hjb.nice.server.controller.portal.CustomerPortalController.PurchaseRequest;
import com.hjb.nice.server.controller.portal.CustomerPortalController.UserProfile;

import java.util.List;

public interface CustomerPortalService {
    UserProfile getProfile(String username);
    void purchasePolicy(String username, PurchaseRequest req);
    List<AutoPolicy> getAutoPolicies(String username);
    List<HomePolicy> getHomePolicies(String username);
    List<InvoiceWithStatus> getInvoices(String username);
    List<Payment> getPayments(String username);
    void makePayment(String username, PaymentRequest req);
}
