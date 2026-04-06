package com.hjb.nice.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerAccount {
    private Integer accountId;
    private Integer customerId;   // 关联 hjb_customer.CUST_ID
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;
}
