package com.hjb.nice.dto;

import lombok.Data;

@Data
public class CustomerRegisterRequest {
    // 账号信息
    private String username;
    private String password;
    private String email;

    // 客户基本信息（暂存于 customer_account，下单时同步到 hjb_customer）
    private String fname;
    private String lname;
    private String gender;
    private String maritalStatus;
    private String addrStreet;
    private String addrCity;
    private String addrState;
    private String zipcode;
}
