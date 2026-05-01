package com.hjb.nice.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerAccount {
    private Integer accountId;
    private Integer customerId;     // FK to hjb_customer.CUST_ID; null until first policy purchase
    private String username;
    private String password;
    private String email;
    // Personal information (collected at registration; synced to hjb_customer on first purchase)
    private String fname;
    private String lname;
    private String gender;
    private String maritalStatus;
    private String addrStreet;
    private String addrCity;
    private String addrState;
    private String zipcode;
    private LocalDateTime createdAt;
}
