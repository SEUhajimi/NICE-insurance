package com.hjb.nice.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HomePolicy {
    private Integer hpId;
    private LocalDate sdate;
    private LocalDate edate;
    private BigDecimal amount;
    private String status;
    private Integer hjbCustomerCustId;
}
