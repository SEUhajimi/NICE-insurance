package com.hjb.nice.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Policy {
    private Integer policyId;
    private LocalDate sdate;
    private LocalDate edate;
    private BigDecimal amount;
    private String status;
    private String policyType;
    private Integer hjbCustomerCustId;
}
