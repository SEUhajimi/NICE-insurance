package com.hjb.nice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度须在 3-50 个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码至少 8 位")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "名字不能为空")
    private String fname;

    @NotBlank(message = "姓氏不能为空")
    private String lname;

    @Pattern(regexp = "^[MF]$", message = "性别须为 M 或 F")
    private String gender;

    @Pattern(regexp = "^[SMW]$", message = "婚姻状况须为 S、M 或 W")
    private String maritalStatus;

    private String addrStreet;
    private String addrCity;
    private String addrState;
    private String zipcode;
}
