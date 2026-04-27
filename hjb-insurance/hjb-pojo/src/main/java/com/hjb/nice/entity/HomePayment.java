package com.hjb.nice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HomePayment {
    @JsonProperty("pId")
    private Integer pId;
    private String method;
    private Integer hjbHomeInvoiceIId;
    private BigDecimal payAmount;
    private LocalDate payDate;
}
