package com.hjb.nice.dto;

import lombok.Data;

@Data
public class CustomerRegisterRequest {
    // 账号信息
    private String username;
    private String password;
    private String email;

    // 客户基本信息
    private String fname;
    private String lname;
    private String gender;
    private String maritalStatus;
    private String custType;
    private String addrStreet;
    private String addrCity;
    private String addrState;
    private String zipcode;
}
