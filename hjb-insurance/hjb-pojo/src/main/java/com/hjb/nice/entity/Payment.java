package com.hjb.nice.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Payment {
    private Integer pId;
    private String method;
    private Integer hjbInvoiceIId;
    private BigDecimal payAmount;
    private LocalDate payDate;
}
