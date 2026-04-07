package com.hjb.nice.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerAccount {
    private Integer accountId;
    private Integer customerId;     // 关联 hjb_customer.CUST_ID，下单前为 null
    private String username;
    private String password;
    private String email;
    // 个人信息（注册时填写，下单时同步到 hjb_customer）
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
