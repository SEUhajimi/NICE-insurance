package com.hjb.nice.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Customer {
    private Integer custId;

    @NotBlank(message = "First name is required")
    private String fname;

    @NotBlank(message = "Last name is required")
    private String lname;
    private String gender;
    private String maritalStatus;
    private String custType;
    private String addrStreet;
    private String addrCity;
    private String addrState;
    private String zipcode;
}
