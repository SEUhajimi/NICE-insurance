package com.hjb.nice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentView {
    @JsonProperty("pId")
    private Integer pId;
    private String method;
    private BigDecimal payAmount;
    private LocalDate payDate;
    private Integer invoiceId;
    private String policyType;
}
