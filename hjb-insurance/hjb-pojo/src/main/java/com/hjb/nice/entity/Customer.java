package com.hjb.nice.entity;

import lombok.Data;

@Data
public class Customer {
    private Integer custId;
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
