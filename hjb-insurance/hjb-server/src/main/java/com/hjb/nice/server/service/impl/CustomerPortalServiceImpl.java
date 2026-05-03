package com.hjb.nice.server.service.impl;

import com.hjb.nice.entity.*;
import com.hjb.nice.enums.CustomerType;
import com.hjb.nice.enums.PolicyStatus;
import com.hjb.nice.server.controller.portal.CustomerPortalController.*;
import com.hjb.nice.server.exception.NotFoundException;
import com.hjb.nice.server.exception.UnauthorizedException;
import com.hjb.nice.server.exception.ValidationException;
import com.hjb.nice.server.mapper.*;
import org.springframework.util.StringUtils;
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
    @Autowired private AutoInvoiceMapper autoInvoiceMapper;
    @Autowired private HomeInvoiceMapper homeInvoiceMapper;
    @Autowired private AutoPaymentMapper autoPaymentMapper;
    @Autowired private HomePaymentMapper homePaymentMapper;
    @Autowired private VehicleMapper vehicleMapper;
    @Autowired private DriverMapper driverMapper;
    @Autowired private HomeMapper homeMapper;

    private CustomerAccount getAccount(String username) {
        CustomerAccount account = customerAccountMapper.findByUsername(username);
        if (account == null) throw new NotFoundException("Account not found");
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
            throw new ValidationException("Invalid policy type; must be AUTO or HOME");
        }
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Policy amount must be greater than 0");
        }

        CustomerAccount account = getAccount(username);

        if (account.getCustomerId() == null) {
            Customer customer = new Customer();
            customer.setFname(account.getFname());
            customer.setLname(account.getLname());
            customer.setGender(account.getGender());
            customer.setMaritalStatus(account.getMaritalStatus());
            customer.setCustType("AUTO".equals(req.getType()) ? "A" : "H");
            customer.setAddrStreet(account.getAddrStreet());
            customer.setAddrCity(account.getAddrCity());
            customer.setAddrState(account.getAddrState());
            customer.setZipcode(account.getZipcode());
            customerMapper.insertForRegister(customer);
            customerAccountMapper.updateCustomerId(account.getAccountId(), customer.getCustId());
            account.setCustomerId(customer.getCustId());
        }

        Integer custId = account.getCustomerId();
        LocalDate today = LocalDate.now();

        if ("AUTO".equals(req.getType())) {
            AutoPolicy policy = new AutoPolicy();
            policy.setSdate(today);
            policy.setEdate(today.plusYears(1));
            policy.setAmount(req.getAmount());
            policy.setStatus(PolicyStatus.CURRENT.getCode());
            policy.setHjbCustomerCustId(custId);
            autoPolicyMapper.insert(policy);

            AutoInvoice invoice = new AutoInvoice();
            invoice.setIDate(today);
            invoice.setDue(today.plusDays(30));
            invoice.setAmount(req.getAmount());
            invoice.setHjbAutopolicyApId(policy.getApId());
            autoInvoiceMapper.insertAutoId(invoice);

            if (StringUtils.hasText(req.getVin())) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVin(req.getVin().toUpperCase().trim());
                vehicle.setMmy(req.getMmy());
                vehicle.setStatus(req.getVehicleStatus());
                vehicle.setHjbAutopolicyApId(policy.getApId());
                vehicleMapper.insert(vehicle);

                if (StringUtils.hasText(req.getDriverLicense())) {
                    Driver driver = new Driver();
                    driver.setDriverLicense(req.getDriverLicense().trim());
                    driver.setFname(req.getDriverFname());
                    driver.setLname(req.getDriverLname());
                    driver.setBirthday(req.getDriverBirthday());
                    driver.setHjbVehicleVin(vehicle.getVin());
                    driverMapper.insert(driver);
                }
            }
        } else {
            HomePolicy policy = new HomePolicy();
            policy.setSdate(today);
            policy.setEdate(today.plusYears(1));
            policy.setAmount(req.getAmount());
            policy.setStatus(PolicyStatus.CURRENT.getCode());
            policy.setHjbCustomerCustId(custId);
            homePolicyMapper.insert(policy);

            HomeInvoice invoice = new HomeInvoice();
            invoice.setIDate(today);
            invoice.setDue(today.plusDays(30));
            invoice.setAmount(req.getAmount());
            invoice.setHjbHomepolicyHpId(policy.getHpId());
            homeInvoiceMapper.insertAutoId(invoice);

            if (req.getPvalue() != null) {
                Home home = new Home();
                home.setPdate(req.getPdate());
                home.setPvalue(req.getPvalue());
                home.setArea(req.getArea());
                home.setHomeType(req.getHomeType());
                home.setAfn(req.getAfn());
                home.setHss(req.getHss());
                home.setSp(req.getSp());
                home.setBasement(req.getBasement());
                home.setHjbHomepolicyHpId(policy.getHpId());
                homeMapper.insert(home);
            }
        }

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
    public List<Vehicle> getVehicles(String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return List.of();
        List<Vehicle> vehicles = new ArrayList<>();
        autoPolicyMapper.findByCustomerId(account.getCustomerId())
                .forEach(ap -> vehicles.addAll(vehicleMapper.findByAutoPolicyId(ap.getApId())));
        return vehicles;
    }

    @Override
    public List<Home> getHomes(String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return List.of();
        List<Home> homes = new ArrayList<>();
        homePolicyMapper.findByCustomerId(account.getCustomerId())
                .forEach(hp -> homes.addAll(homeMapper.findByHomePolicyId(hp.getHpId())));
        return homes;
    }

    @Override
    public List<InvoiceWithStatus> getInvoices(String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return List.of();
        Integer custId = account.getCustomerId();
        List<InvoiceWithStatus> result = new ArrayList<>();

        autoPolicyMapper.findByCustomerId(custId).forEach(ap ->
            autoInvoiceMapper.findByAutoPolicyId(ap.getApId()).forEach(inv ->
                result.add(buildAutoInvoiceStatus(inv))));

        homePolicyMapper.findByCustomerId(custId).forEach(hp ->
            homeInvoiceMapper.findByHomePolicyId(hp.getHpId()).forEach(inv ->
                result.add(buildHomeInvoiceStatus(inv))));

        return result;
    }

    @Override
    public List<PaymentView> getPayments(String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return List.of();
        Integer custId = account.getCustomerId();
        List<PaymentView> payments = new ArrayList<>();

        autoPolicyMapper.findByCustomerId(custId).forEach(ap ->
            autoInvoiceMapper.findByAutoPolicyId(ap.getApId()).forEach(inv ->
                autoPaymentMapper.findByInvoiceId(inv.getIId()).forEach(p ->
                    payments.add(toPaymentView(p, inv.getIId(), "Auto")))));

        homePolicyMapper.findByCustomerId(custId).forEach(hp ->
            homeInvoiceMapper.findByHomePolicyId(hp.getHpId()).forEach(inv ->
                homePaymentMapper.findByInvoiceId(inv.getIId()).forEach(p ->
                    payments.add(toPaymentView(p, inv.getIId(), "Home")))));

        return payments;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void makePayment(String username, PaymentRequest req) {
        if (req.getPayAmount() == null || req.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be greater than 0");
        }
        if (req.getPayAmount().scale() > 2) {
            throw new ValidationException("Payment amount may have at most two decimal places");
        }

        String type = req.getType();
        if (!"AUTO".equals(type) && !"HOME".equals(type)) {
            throw new ValidationException("Invalid invoice type; must be AUTO or HOME");
        }

        if ("AUTO".equals(type)) {
            AutoInvoice invoice = autoInvoiceMapper.findById(req.getInvoiceId());
            if (invoice == null) throw new NotFoundException("Auto Invoice not found");
            if (!autoInvoiceBelongsToUser(invoice, username)) {
                throw new UnauthorizedException("You are not authorized to operate on this invoice");
            }
            BigDecimal paid = autoPaymentMapper.findByInvoiceId(req.getInvoiceId()).stream()
                    .map(AutoPayment::getPayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (paid.compareTo(invoice.getAmount()) >= 0) throw new ValidationException("This invoice has already been paid in full");
            BigDecimal remaining = invoice.getAmount().subtract(paid);
            if (req.getPayAmount().compareTo(remaining) > 0) {
                throw new ValidationException("Payment amount cannot exceed the remaining balance of " + remaining);
            }
            AutoPayment payment = new AutoPayment();
            payment.setHjbAutoInvoiceIId(req.getInvoiceId());
            payment.setMethod(req.getMethod());
            payment.setPayAmount(req.getPayAmount());
            payment.setPayDate(LocalDate.now());
            autoPaymentMapper.insertAutoId(payment);
        } else {
            HomeInvoice invoice = homeInvoiceMapper.findById(req.getInvoiceId());
            if (invoice == null) throw new NotFoundException("Home Invoice not found");
            if (!homeInvoiceBelongsToUser(invoice, username)) {
                throw new UnauthorizedException("You are not authorized to operate on this invoice");
            }
            BigDecimal paid = homePaymentMapper.findByInvoiceId(req.getInvoiceId()).stream()
                    .map(HomePayment::getPayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (paid.compareTo(invoice.getAmount()) >= 0) throw new ValidationException("This invoice has already been paid in full");
            BigDecimal remaining = invoice.getAmount().subtract(paid);
            if (req.getPayAmount().compareTo(remaining) > 0) {
                throw new ValidationException("Payment amount cannot exceed the remaining balance of " + remaining);
            }
            HomePayment payment = new HomePayment();
            payment.setHjbHomeInvoiceIId(req.getInvoiceId());
            payment.setMethod(req.getMethod());
            payment.setPayAmount(req.getPayAmount());
            payment.setPayDate(LocalDate.now());
            homePaymentMapper.insertAutoId(payment);
        }
    }

    private boolean autoInvoiceBelongsToUser(AutoInvoice invoice, String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return false;
        return autoPolicyMapper.findByIdAndCustomerId(invoice.getHjbAutopolicyApId(), account.getCustomerId()) != null;
    }

    private boolean homeInvoiceBelongsToUser(HomeInvoice invoice, String username) {
        CustomerAccount account = getAccount(username);
        if (account.getCustomerId() == null) return false;
        return homePolicyMapper.findByIdAndCustomerId(invoice.getHjbHomepolicyHpId(), account.getCustomerId()) != null;
    }

    private InvoiceWithStatus buildAutoInvoiceStatus(AutoInvoice inv) {
        List<AutoPayment> payments = autoPaymentMapper.findByInvoiceId(inv.getIId());
        BigDecimal paidAmount = payments.stream()
                .map(AutoPayment::getPayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<PaymentView> paymentViews = payments.stream()
                .map(p -> toPaymentView(p, inv.getIId(), "Auto")).toList();

        InvoiceWithStatus dto = new InvoiceWithStatus();
        dto.setIId(inv.getIId());
        dto.setIDate(inv.getIDate());
        dto.setDue(inv.getDue());
        dto.setAmount(inv.getAmount());
        dto.setHjbAutopolicyApId(inv.getHjbAutopolicyApId());
        dto.setPolicyType("Auto");
        dto.setPaidAmount(paidAmount);
        dto.setPaid(paidAmount.compareTo(inv.getAmount()) >= 0);
        dto.setPayments(paymentViews);
        return dto;
    }

    private InvoiceWithStatus buildHomeInvoiceStatus(HomeInvoice inv) {
        List<HomePayment> payments = homePaymentMapper.findByInvoiceId(inv.getIId());
        BigDecimal paidAmount = payments.stream()
                .map(HomePayment::getPayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<PaymentView> paymentViews = payments.stream()
                .map(p -> toPaymentView(p, inv.getIId(), "Home")).toList();

        InvoiceWithStatus dto = new InvoiceWithStatus();
        dto.setIId(inv.getIId());
        dto.setIDate(inv.getIDate());
        dto.setDue(inv.getDue());
        dto.setAmount(inv.getAmount());
        dto.setHjbHomepolicyHpId(inv.getHjbHomepolicyHpId());
        dto.setPolicyType("Home");
        dto.setPaidAmount(paidAmount);
        dto.setPaid(paidAmount.compareTo(inv.getAmount()) >= 0);
        dto.setPayments(paymentViews);
        return dto;
    }

    private PaymentView toPaymentView(AutoPayment p, Integer invoiceId, String policyType) {
        PaymentView v = new PaymentView();
        v.setPId(p.getPId());
        v.setMethod(p.getMethod());
        v.setPayAmount(p.getPayAmount());
        v.setPayDate(p.getPayDate());
        v.setInvoiceId(invoiceId);
        v.setPolicyType(policyType);
        return v;
    }

    private PaymentView toPaymentView(HomePayment p, Integer invoiceId, String policyType) {
        PaymentView v = new PaymentView();
        v.setPId(p.getPId());
        v.setMethod(p.getMethod());
        v.setPayAmount(p.getPayAmount());
        v.setPayDate(p.getPayDate());
        v.setInvoiceId(invoiceId);
        v.setPolicyType(policyType);
        return v;
    }
}
