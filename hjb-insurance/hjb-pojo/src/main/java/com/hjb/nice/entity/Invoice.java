package com.hjb.nice.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Invoice {
    private Integer iId;
    private LocalDate iDate;
    private LocalDate due;
    private BigDecimal amount;
    private Integer hjbHomepolicyHpId;
    private Integer hjbAutopolicyApId;
}
